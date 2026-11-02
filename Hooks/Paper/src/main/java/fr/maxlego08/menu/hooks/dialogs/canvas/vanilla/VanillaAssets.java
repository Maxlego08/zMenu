package fr.maxlego08.menu.hooks.dialogs.canvas.vanilla;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.common.canvas.TextureMetrics;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.Comparator;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * The fonts and textures of the vanilla client, read from the client jar Mojang publishes for the server's
 * Minecraft version. Only the files canvases need are kept, in {@code cache/vanilla/<version>}; the generated
 * resource pack points at the client's own copies instead, except the {@code gui} shader, which
 * {@code dialog-canvas.hide-focus-outline} patches into the pack.
 */
public final class VanillaAssets {

    private static final String MANIFEST = "https://piston-meta.mojang.com/mc/game/version_manifest_v2.json";
    private static final List<String> KEPT_FOLDERS = List.of(
            "assets/minecraft/font/",
            "assets/minecraft/textures/font/",
            "assets/minecraft/textures/item/",
            "assets/minecraft/textures/block/",
            "assets/minecraft/textures/gui/",
            "assets/minecraft/textures/mob_effect/",
            "assets/minecraft/shaders/core/"
    );
    private static final String COMPLETE_MARKER = ".complete-2";
    private static final Gson GSON = new Gson();

    private final Path folder;
    private final String version;
    private final Map<Key, Optional<BufferedImage>> textures = new ConcurrentHashMap<>();
    private volatile boolean ready;
    private volatile Function<String, Optional<byte[]>> overrides = path -> Optional.empty();

    public VanillaAssets(@NotNull Path cacheFolder, @NotNull String version) {
        this.folder = cacheFolder.resolve("vanilla").resolve(version);
        this.version = version;
        this.ready = Files.exists(this.folder.resolve(COMPLETE_MARKER));
    }

    /**
     * @return True once the files are on disk.
     */
    public boolean isReady() {
        return this.ready;
    }

    /**
     * @return The Minecraft version the files belong to.
     */
    public @NotNull String getVersion() {
        return this.version;
    }

    /**
     * Downloads the client jar and keeps the files canvases need. Blocks while downloading, so it must run
     * off the main thread. Does nothing when the files are already there.
     *
     * @return True if the files are ready afterwards.
     */
    public synchronized boolean download() {
        if (this.ready) return true;
        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).followRedirects(HttpClient.Redirect.NORMAL).build();
        Path temporary = this.folder.resolveSibling(this.version + ".download");
        try {
            JsonObject manifest = this.getJson(client, MANIFEST);
            String versionUrl = null;
            for (JsonElement element : manifest.getAsJsonArray("versions")) {
                JsonObject entry = element.getAsJsonObject();
                if (this.version.equals(entry.get("id").getAsString())) {
                    versionUrl = entry.get("url").getAsString();
                    break;
                }
            }
            if (versionUrl == null) {
                Logger.info("Mojang does not list the Minecraft version " + this.version + ", dialog canvases cannot measure the default font", Logger.LogType.WARNING);
                return false;
            }

            JsonObject clientDownload = this.getJson(client, versionUrl).getAsJsonObject("downloads").getAsJsonObject("client");
            String jarUrl = clientDownload.get("url").getAsString();
            String expectedSha1 = clientDownload.get("sha1").getAsString();

            Logger.info("Downloading the fonts and textures of the Minecraft " + this.version + " client for dialog canvases...");
            deleteRecursively(temporary);
            Files.createDirectories(temporary);

            HttpRequest request = HttpRequest.newBuilder(URI.create(jarUrl)).timeout(Duration.ofMinutes(5)).GET().build();
            HttpResponse<InputStream> response = client.send(request, HttpResponse.BodyHandlers.ofInputStream());
            if (response.statusCode() != 200) {
                Logger.info("Could not download the Minecraft " + this.version + " client, HTTP " + response.statusCode(), Logger.LogType.WARNING);
                return false;
            }

            MessageDigest digest = MessageDigest.getInstance("SHA-1");
            try (DigestInputStream digestStream = new DigestInputStream(response.body(), digest); ZipInputStream zip = new ZipInputStream(digestStream)) {
                ZipEntry entry;
                while ((entry = zip.getNextEntry()) != null) {
                    String name = entry.getName();
                    if (entry.isDirectory() || name.contains("..") || KEPT_FOLDERS.stream().noneMatch(name::startsWith)) continue;
                    Path target = temporary.resolve(name);
                    Files.createDirectories(target.getParent());
                    Files.copy(zip, target, StandardCopyOption.REPLACE_EXISTING);
                }
                digestStream.transferTo(OutputStream.nullOutputStream());
            }

            String sha1 = HexFormat.of().formatHex(digest.digest());
            if (!sha1.equalsIgnoreCase(expectedSha1)) {
                Logger.info("The downloaded Minecraft " + this.version + " client does not match its checksum, it was discarded", Logger.LogType.WARNING);
                deleteRecursively(temporary);
                return false;
            }

            Files.writeString(temporary.resolve(COMPLETE_MARKER), sha1, StandardCharsets.UTF_8);
            deleteRecursively(this.folder);
            Files.createDirectories(this.folder.getParent());
            Files.move(temporary, this.folder, StandardCopyOption.REPLACE_EXISTING);
            this.ready = true;
            this.textures.clear();
            Logger.info("The fonts and textures of the Minecraft " + this.version + " client are ready");
            return true;
        } catch (IOException | NoSuchAlgorithmException | RuntimeException exception) {
            Logger.info("Could not download the Minecraft " + this.version + " client: " + exception.getMessage(), Logger.LogType.WARNING);
            return false;
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            return false;
        } finally {
            try {
                deleteRecursively(temporary);
            } catch (IOException ignored) {
            }
        }
    }

    private @NotNull JsonObject getJson(@NotNull HttpClient client, @NotNull String url) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder(URI.create(url)).timeout(Duration.ofSeconds(30)).GET().build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() != 200) throw new IOException("HTTP " + response.statusCode() + " for " + url);
        return GSON.fromJson(response.body(), JsonObject.class);
    }

    /**
     * Sets where the files the server's resource pack puts over the client's own are read from. Fonts and
     * textures are read from there first, so canvases measure what players see.
     *
     * @param overrides Reads a file of the server's pack by its path, such as
     *                  {@code assets/minecraft/textures/font/ascii.png}.
     */
    public void useOverrides(@NotNull Function<String, Optional<byte[]>> overrides) {
        this.overrides = overrides;
        this.textures.clear();
    }

    private @NotNull Optional<byte[]> override(@NotNull String path) {
        try {
            return this.overrides.apply(path);
        } catch (RuntimeException | LinkageError exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a JSON file of the client, or the server's pack's copy of it.
     *
     * @param path The path inside the jar, such as {@code assets/minecraft/font/include/default.json}.
     * @return The file, or an empty result if it is not there.
     */
    public @NotNull Optional<JsonObject> json(@NotNull String path) {
        Optional<byte[]> override = this.override(path);
        if (override.isPresent()) return parse(new String(override.get(), StandardCharsets.UTF_8));
        if (!this.ready) return Optional.empty();
        Path file = this.folder.resolve(path);
        if (!Files.isRegularFile(file)) return Optional.empty();
        try {
            return parse(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private static @NotNull Optional<JsonObject> parse(@NotNull String json) {
        try {
            return Optional.ofNullable(GSON.fromJson(json, JsonObject.class));
        } catch (RuntimeException exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a text file of the client, such as a shader.
     *
     * @param path The path inside the jar, such as {@code assets/minecraft/shaders/core/gui.fsh}.
     * @return The text, or an empty result if it is not there.
     */
    public @NotNull Optional<String> text(@NotNull String path) {
        if (!this.ready) return Optional.empty();
        Path file = this.folder.resolve(path);
        if (!Files.isRegularFile(file)) return Optional.empty();
        try {
            return Optional.of(Files.readString(file, StandardCharsets.UTF_8));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    /**
     * Reads a texture of the client, or the server's pack's copy of it.
     *
     * @param texture The texture as a font names it, such as {@code minecraft:font/ascii.png}.
     * @return The image, or an empty result if it is not there.
     */
    public @NotNull Optional<BufferedImage> texture(@NotNull Key texture) {
        return this.textures.computeIfAbsent(texture, key -> {
            Optional<byte[]> override = this.override(texturePath(key));
            if (override.isPresent()) return Optional.ofNullable(TextureMetrics.read(override.get()));
            if (!this.ready || !key.namespace().equals(Key.MINECRAFT_NAMESPACE)) return Optional.empty();
            Path file = this.folder.resolve(texturePath(key));
            if (!Files.isRegularFile(file)) return Optional.empty();
            try {
                return Optional.ofNullable(TextureMetrics.read(Files.readAllBytes(file)));
            } catch (IOException exception) {
                return Optional.empty();
            }
        });
    }

    /**
     * @param texture The texture.
     * @return True if the client has an animation file for this texture, which then holds its frames
     * stacked vertically.
     */
    public boolean isAnimated(@NotNull Key texture) {
        String meta = texturePath(texture) + ".mcmeta";
        if (this.override(texturePath(texture)).isPresent()) {
            return this.override(meta).flatMap(bytes -> parse(new String(bytes, StandardCharsets.UTF_8))).map(json -> json.has("animation")).orElse(false);
        }
        return this.json(meta).map(json -> json.has("animation")).orElse(false);
    }

    private static @NotNull String texturePath(@NotNull Key texture) {
        return "assets/" + texture.namespace() + "/textures/" + texture.value();
    }

    /**
     * Reads the bitmap providers of a client font, following its references.
     *
     * @param font The font, such as {@code minecraft:include/default}.
     * @return The providers, in order.
     */
    public @NotNull List<JsonObject> providers(@NotNull Key font) {
        Optional<JsonObject> json = this.json("assets/" + font.namespace() + "/font/" + font.value() + ".json");
        if (json.isEmpty() || !json.get().has("providers")) return List.of();
        JsonArray providers = json.get().getAsJsonArray("providers");
        return providers.asList().stream().filter(JsonElement::isJsonObject).map(JsonElement::getAsJsonObject).toList();
    }

    private static void deleteRecursively(@NotNull Path path) throws IOException {
        if (!Files.exists(path)) return;
        try (Stream<Path> stream = Files.walk(path)) {
            for (Path child : stream.sorted(Comparator.reverseOrder()).toList()) Files.deleteIfExists(child);
        }
    }

}
