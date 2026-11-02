package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The resource pack format of each Minecraft version able to show dialogs, for the {@code pack.mcmeta}
 * of the standalone pack.
 */
public final class PackFormat {

    private static final Map<String, Integer> FORMATS = new LinkedHashMap<>();
    private static final int SPLIT_FORMAT = 65;

    static {
        FORMATS.put("1.21.6", 63);
        FORMATS.put("1.21.7", 64);
        FORMATS.put("1.21.9", 69);
        FORMATS.put("1.21.11", 75);
        FORMATS.put("26.1", 84);
        FORMATS.put("26.2", 88);
        FORMATS.put("26.3", 97);
    }

    private PackFormat() {
    }

    /**
     * @param version A Minecraft version.
     * @return The pack format of the newest known version not newer than it.
     */
    public static int forVersion(@NotNull String version) {
        MinecraftVersion target = MinecraftVersion.parse(version);
        int format = FORMATS.values().iterator().next();
        for (Map.Entry<String, Integer> entry : FORMATS.entrySet()) {
            if (target.isAtLeast(MinecraftVersion.parse(entry.getKey()))) format = entry.getValue();
        }
        return format;
    }

    /**
     * @param format      The pack format of the server.
     * @param description The description shown in the client.
     * @return The content of {@code pack.mcmeta}.
     */
    public static @NotNull JsonObject mcmeta(int format, @NotNull String description) {
        JsonObject pack = new JsonObject();
        pack.addProperty("description", description);
        if (format < SPLIT_FORMAT) {
            pack.addProperty("pack_format", format);
            JsonArray supported = new JsonArray();
            supported.add(format);
            supported.add(format);
            pack.add("supported_formats", supported);
        } else {
            pack.addProperty("min_format", format);
            pack.addProperty("max_format", format);
        }
        JsonObject root = new JsonObject();
        root.add("pack", pack);
        return root;
    }
}
