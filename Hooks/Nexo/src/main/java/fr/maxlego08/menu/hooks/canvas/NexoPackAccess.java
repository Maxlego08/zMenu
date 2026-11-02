package fr.maxlego08.menu.hooks.canvas;

import fr.maxlego08.menu.common.canvas.TextureMetrics;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

/**
 * Reads Nexo's resource pack. Nexo describes it with its own copy of the creative library, which zMenu does
 * not compile against, so the pack is only reached by reflection.
 */
final class NexoPackAccess {

    private static volatile Object lastPack;

    private NexoPackAccess() {
    }

    /**
     * Keeps the pack Nexo is building, to read its textures.
     *
     * @param event A {@code NexoPostPackGenerateEvent}.
     */
    static void remember(@NotNull Object event) {
        Object pack = invoke(event, "getResourcePack");
        if (pack != null) lastPack = pack;
    }

    /**
     * Adds a folder to the pack Nexo is building.
     *
     * @param event  A {@code NexoPostPackGenerateEvent}.
     * @param folder The folder, holding {@code assets/...}.
     * @return True if Nexo accepted it.
     */
    static boolean addFolder(@NotNull Object event, @NotNull File folder) {
        try {
            Method method = event.getClass().getMethod("addResourcePack", File.class);
            return Boolean.TRUE.equals(method.invoke(event, folder));
        } catch (ReflectiveOperationException | RuntimeException exception) {
            return false;
        }
    }

    /**
     * Reads a texture of Nexo's pack, from the last pack Nexo built, or from its {@code pack} folder.
     *
     * @param texture The texture, such as {@code minecraft:required/ui/menu_items.png}.
     * @return The image, or an empty result.
     */
    static @NotNull Optional<BufferedImage> readTexture(@NotNull Key texture) {
        return readFile("assets/" + texture.namespace() + "/textures/" + texture.value()).map(TextureMetrics::read);
    }

    /**
     * Reads a file of Nexo's pack: a texture from the last pack Nexo built, anything else from its
     * {@code pack} folder.
     *
     * @param path The path inside the pack, such as {@code assets/minecraft/textures/font/ascii.png}.
     * @return The bytes, or an empty result.
     */
    static @NotNull Optional<byte[]> readFile(@NotNull String path) {
        Object pack = lastPack;
        Key texture = textureKey(path);
        if (pack != null && texture != null) {
            Object writable = invoke(invoke(pack, "texture", texture), "data");
            if (invoke(writable, "toByteArray") instanceof byte[] data) return Optional.of(data);
        }

        Plugin nexo = Bukkit.getPluginManager().getPlugin("Nexo");
        if (nexo == null || path.contains("..")) return Optional.empty();
        Path file = nexo.getDataFolder().toPath().resolve("pack").resolve(path);
        if (!Files.isRegularFile(file)) return Optional.empty();
        try {
            return Optional.of(Files.readAllBytes(file));
        } catch (IOException exception) {
            return Optional.empty();
        }
    }

    private static @Nullable Key textureKey(@NotNull String path) {
        String[] parts = path.split("/", 4);
        if (parts.length < 4 || !parts[0].equals("assets") || !parts[2].equals("textures")) return null;
        if (!Key.parseableNamespace(parts[1]) || !Key.parseableValue(parts[3])) return null;
        return Key.key(parts[1], parts[3]);
    }

    private static @Nullable Object invoke(@Nullable Object target, @NotNull String name, Object... arguments) {
        if (target == null) return null;
        try {
            for (Method method : target.getClass().getMethods()) {
                if (!method.getName().equals(name) || method.getParameterCount() != arguments.length) continue;
                Class<?>[] types = method.getParameterTypes();
                boolean matches = true;
                for (int index = 0; index < types.length; index++) {
                    if (!types[index].isInstance(arguments[index])) matches = false;
                }
                if (matches) return method.invoke(target, arguments);
            }
        } catch (ReflectiveOperationException | RuntimeException ignored) {
        }
        return null;
    }
}
