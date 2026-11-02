package fr.maxlego08.menu.hooks.itemsadder.canvas;

import fr.maxlego08.menu.common.canvas.TextureMetrics;
import net.kyori.adventure.key.Key;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Stream;

/**
 * The font images of ItemsAdder, read from its {@code contents} folder. ItemsAdder's API gives neither the
 * texture nor the position of a font image, so they are read from the same files ItemsAdder reads.
 */
final class ItemsAdderFontIndex {

    private final Path contents;
    private volatile Map<String, Entry> entries = Map.of();

    ItemsAdderFontIndex(@NotNull Path itemsAdderFolder) {
        this.contents = itemsAdderFolder.resolve("contents");
    }

    /**
     * Reads every configuration file of the {@code contents} folder again.
     */
    public void reload() {
        Map<String, Entry> loaded = new HashMap<>();
        if (Files.isDirectory(this.contents)) {
            List<Path> folders;
            try (Stream<Path> stream = Files.list(this.contents)) {
                folders = stream.filter(Files::isDirectory).toList();
            } catch (IOException exception) {
                folders = List.of();
            }
            for (Path folder : folders) this.readFolder(folder, loaded);
        }
        this.entries = Map.copyOf(loaded);
    }

    private void readFolder(@NotNull Path folder, @NotNull Map<String, Entry> loaded) {
        List<Path> files;
        try (Stream<Path> stream = Files.walk(folder)) {
            files = stream.filter(path -> path.toString().endsWith(".yml")).filter(path -> !path.toString().replace('\\', '/').contains("/resourcepack/")).toList();
        } catch (IOException exception) {
            return;
        }
        for (Path file : files) {
            YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file.toFile());
            String namespace = configuration.getString("info.namespace");
            ConfigurationSection images = configuration.getConfigurationSection("font_images");
            if (namespace == null || images == null) continue;
            for (String id : images.getKeys(false)) {
                ConfigurationSection image = images.getConfigurationSection(id);
                if (image == null) continue;
                String path = image.getString("path");
                if (path == null) continue;
                String textureNamespace = namespace;
                String texturePath = path;
                int separator = path.indexOf(':');
                if (separator > 0) {
                    textureNamespace = path.substring(0, separator);
                    texturePath = path.substring(separator + 1);
                }
                if (!texturePath.endsWith(".png")) texturePath += ".png";
                if (!Key.parseableNamespace(textureNamespace) || !Key.parseableValue(texturePath)) continue;
                Key texture = Key.key(textureNamespace, texturePath);
                loaded.put(namespace + ":" + id, new Entry(texture, image.getInt("scale_ratio", 9), image.getInt("y_position", 8), folder));
            }
        }
    }

    /**
     * @param id A font image, {@code namespace:id}.
     * @return What its configuration says, or null if no configuration file defines it.
     */
    @Nullable Entry get(@NotNull String id) {
        return this.entries.get(id);
    }

    /**
     * Finds the PNG of a texture in the {@code contents} folder, where ItemsAdder lets it be.
     *
     * @param texture The texture.
     * @param folder  The content folder defining it, searched first.
     * @return The image, or an empty result.
     */
    @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture, @Nullable Path folder) {
        String namespace = texture.namespace();
        String path = texture.value();
        List<Path> folders = new ArrayList<>();
        if (folder != null) folders.add(folder);
        try (Stream<Path> stream = Files.isDirectory(this.contents) ? Files.list(this.contents) : Stream.empty()) {
            stream.filter(Files::isDirectory).filter(candidate -> !candidate.equals(folder)).forEach(folders::add);
        } catch (IOException ignored) {
        }

        for (Path candidate : folders) {
            for (Path file : List.of(
                    candidate.resolve("textures").resolve(path),
                    candidate.resolve("resourcepack/assets").resolve(namespace).resolve("textures").resolve(path),
                    candidate.resolve("resourcepack").resolve(namespace).resolve("textures").resolve(path),
                    candidate.resolve("assets").resolve(namespace).resolve("textures").resolve(path),
                    candidate.resolve(namespace).resolve("textures").resolve(path))) {
                if (!Files.isRegularFile(file)) continue;
                try {
                    BufferedImage image = TextureMetrics.read(Files.readAllBytes(file));
                    if (image != null) return Optional.of(image);
                } catch (IOException ignored) {
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Finds a file one of the content folders puts in ItemsAdder's pack.
     *
     * @param path    The path inside the pack, such as {@code assets/minecraft/textures/font/ascii.png}.
     * @param skipped The content folder left out, zMenu's own.
     * @return The bytes, or an empty result.
     */
    @NotNull Optional<byte[]> readPackFile(@NotNull String path, @NotNull String skipped) {
        if (!path.startsWith("assets/") || path.contains("..") || !Files.isDirectory(this.contents)) return Optional.empty();
        String inAssets = path.substring("assets/".length());
        List<Path> folders;
        try (Stream<Path> stream = Files.list(this.contents)) {
            folders = stream.filter(Files::isDirectory).filter(folder -> !folder.getFileName().toString().equals(skipped)).sorted().toList();
        } catch (IOException exception) {
            return Optional.empty();
        }
        for (Path folder : folders) {
            for (Path file : List.of(folder.resolve("resourcepack").resolve(path), folder.resolve("resourcepack").resolve(inAssets), folder.resolve(path))) {
                if (!Files.isRegularFile(file)) continue;
                try {
                    return Optional.of(Files.readAllBytes(file));
                } catch (IOException ignored) {
                }
            }
        }
        return Optional.empty();
    }

    /**
     * A font image as its configuration defines it.
     *
     * @param texture The texture.
     * @param height  The height it is drawn at ({@code scale_ratio}).
     * @param ascent  Its ascent ({@code y_position}).
     * @param folder  The content folder defining it.
     */
    record Entry(@NotNull Key texture, int height, int ascent, @NotNull Path folder) {
    }
}
