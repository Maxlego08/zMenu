package fr.maxlego08.menu.hooks.dialogs.canvas.pack;

import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The files of one generation.
 *
 * @param assets      The folder holding {@code assets/zmenu}.
 * @param zip         The standalone pack.
 * @param sha1        The SHA-1 of the standalone pack.
 * @param contentHash The hash of the generated files.
 * @param changed     True if the files differ from the previous generation.
 * @param isEmpty     True if no canvas needs a file.
 */
public record ZGeneratedPack(@NotNull Path assets, @NotNull Path zip, @NotNull String sha1, @NotNull String contentHash, boolean changed,
                             boolean isEmpty) implements GeneratedPack {
}
