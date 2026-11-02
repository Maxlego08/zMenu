package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.TreeMap;

/**
 * The characters given to the glyphs of canvases, kept in {@code resource-pack/codepoints.lock}.
 * <p>
 * The file only grows: a glyph keeps its character for as long as the file exists, even after the dialog
 * using it is deleted. A player who still has the previous pack therefore sees every glyph that did not
 * change in the right place.
 */
public final class CodepointLock {

    public static final int FIRST = 0xF0000;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private final Path file;
    private final Map<String, Integer> codepoints = new TreeMap<>();
    private int next = FIRST;
    private boolean dirty;

    private CodepointLock(@NotNull Path file) {
        this.file = file;
    }

    /**
     * Reads the lock file, or starts an empty one if it does not exist.
     *
     * @param file The lock file.
     * @return The lock.
     * @throws IOException If the file exists but cannot be read.
     */
    public static @NotNull CodepointLock load(@NotNull Path file) throws IOException {
        CodepointLock lock = new CodepointLock(file);
        if (!Files.exists(file)) return lock;
        try {
            JsonObject root = GSON.fromJson(Files.readString(file, StandardCharsets.UTF_8), JsonObject.class);
            if (root == null) return lock;
            JsonObject entries = root.getAsJsonObject("codepoints");
            if (entries != null) {
                for (String key : entries.keySet()) {
                    int codepoint = entries.get(key).getAsInt();
                    lock.codepoints.put(key, codepoint);
                    lock.next = Math.max(lock.next, codepoint + 1);
                }
            }
            if (root.has("next")) lock.next = Math.max(lock.next, root.get("next").getAsInt());
        } catch (JsonParseException | IllegalStateException | ClassCastException exception) {
            throw new IOException("The file " + file + " is not a valid codepoint lock: " + exception.getMessage(), exception);
        }
        return lock;
    }

    /**
     * @param key The identity of a glyph variant.
     * @return Its character, given now if the key is new.
     * @throws IllegalStateException If plane 15 is full.
     */
    public synchronized int codepoint(@NotNull String key) {
        Integer existing = this.codepoints.get(key);
        if (existing != null) return existing;
        if (this.next >= SpaceFont.FIRST_RESERVED) {
            throw new IllegalStateException("No character left for canvas glyphs, delete resource-pack/codepoints.lock to start again");
        }
        int codepoint = this.next++;
        this.codepoints.put(key, codepoint);
        this.dirty = true;
        return codepoint;
    }

    /**
     * Writes the lock file if a character was given since it was read.
     *
     * @throws IOException If the file cannot be written.
     */
    public synchronized void save() throws IOException {
        if (!this.dirty) return;
        JsonObject entries = new JsonObject();
        this.codepoints.forEach(entries::addProperty);
        JsonObject root = new JsonObject();
        root.addProperty("version", 1);
        root.addProperty("next", this.next);
        root.add("codepoints", entries);
        Files.createDirectories(this.file.getParent());
        Files.writeString(this.file, GSON.toJson(root), StandardCharsets.UTF_8);
        this.dirty = false;
    }
}
