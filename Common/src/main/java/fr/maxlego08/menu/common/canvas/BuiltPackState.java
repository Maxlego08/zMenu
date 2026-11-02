package fr.maxlego08.menu.common.canvas;

import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Remembers which canvas files a resource pack plugin last built its pack with, across restarts, so a
 * pack target knows whether that plugin must rebuild to give players the current files.
 */
public final class BuiltPackState {

    private final Path file;
    private volatile String builtHash;

    /**
     * @param resourcePackFolder The {@code resource-pack} folder of zMenu.
     * @param target             The name of the target.
     */
    public BuiltPackState(@NotNull Path resourcePackFolder, @NotNull String target) {
        this.file = resourcePackFolder.resolve(".built-" + target);
        try {
            this.builtHash = Files.isRegularFile(this.file) ? Files.readString(this.file, StandardCharsets.UTF_8).trim() : null;
        } catch (IOException exception) {
            this.builtHash = null;
        }
    }

    /**
     * Records that the plugin built its pack with these files.
     *
     * @param pack The files.
     */
    public void built(@NotNull GeneratedPack pack) {
        this.built(pack.contentHash());
    }

    /**
     * Records that the plugin built its pack with the files of this hash.
     *
     * @param contentHash The {@link GeneratedPack#contentHash()} of the files.
     */
    public void built(@Nullable String contentHash) {
        if (contentHash == null || contentHash.equals(this.builtHash)) return;
        this.builtHash = contentHash;
        try {
            Files.createDirectories(this.file.getParent());
            Files.writeString(this.file, contentHash, StandardCharsets.UTF_8);
        } catch (IOException ignored) {
        }
    }

    /**
     * @param pack The current files.
     * @return True if the plugin last built its pack with other files.
     */
    public boolean isOutdated(@NotNull GeneratedPack pack) {
        if (pack.isEmpty() && this.builtHash == null) return false;
        return !pack.contentHash().equals(this.builtHash);
    }
}
