package fr.maxlego08.menu.api.dialog.canvas;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * The files the canvas manager generated for the canvases of the loaded dialogs.
 */
public interface GeneratedPack {

    /**
     * @return The folder holding {@code assets/zmenu/...}, without {@code pack.mcmeta}, which is what a
     * plugin building its own resource pack imports.
     */
    @NotNull
    Path assets();

    /**
     * @return A complete resource pack with {@code pack.mcmeta}, for servers that send it themselves.
     */
    @NotNull
    Path zip();

    /**
     * @return The SHA-1 of {@link #zip()}, in lowercase hexadecimal.
     */
    @NotNull
    String sha1();

    /**
     * @return A hash of the generated files, equal between two generations that wrote the same files.
     */
    @NotNull
    String contentHash();

    /**
     * @return True if the files differ from the previous generation.
     */
    boolean changed();

    /**
     * @return True if the canvases of the loaded dialogs need no file at all.
     */
    boolean isEmpty();
}
