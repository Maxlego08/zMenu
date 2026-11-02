package fr.maxlego08.menu.api.dialog.canvas;

import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Delivers the generated canvas files to players, usually by adding them to the resource pack another
 * plugin builds and hosts (Nexo, ItemsAdder, CraftEngine).
 * <p>
 * The target is chosen by {@code dialog-canvas.resource-pack.target} in {@code config.yml}, by the value
 * of {@link #key()}.
 */
public interface CanvasPackTarget extends Keyed {

    /**
     * @return True if the plugin this target writes to is installed and enabled.
     */
    boolean isAvailable();

    /**
     * Called after every generation while this target is the active one.
     *
     * @param pack The generated files.
     * @return True if the pack players download does not hold these files yet, so the plugin this target
     * writes to has to rebuild it.
     */
    boolean install(@NotNull GeneratedPack pack);

    /**
     * Asks the plugin this target writes to to rebuild its resource pack. Only called when
     * {@code dialog-canvas.resource-pack.auto-rebuild} is enabled.
     */
    void requestRebuild();

    /**
     * @return What the server owner has to do for players to get the new files, such as
     * {@code run /iazip}.
     */
    @NotNull
    String rebuildHint();

    /**
     * Reads a file the pack of the plugin this target writes to puts over the client's own, so canvases
     * measure the vanilla fonts and textures players actually see. zMenu's own files are never returned.
     *
     * @param path The path inside the pack, such as {@code assets/minecraft/textures/font/ascii.png}.
     * @return The bytes of the file, or an empty result if the pack does not replace it.
     */
    default @NotNull Optional<byte[]> readPackFile(@NotNull String path) {
        return Optional.empty();
    }
}
