package fr.maxlego08.menu.hooks.dialogs.canvas.target;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.dialog.canvas.CanvasPackTarget;
import fr.maxlego08.menu.api.dialog.canvas.GeneratedPack;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * The server owner adds zMenu's files to their resource pack by hand.
 */
public final class NoneCanvasPackTarget implements CanvasPackTarget {

    public static final String NAME = "none";

    @Override
    public @NotNull Key key() {
        return ZMenuKeys.key(NAME);
    }

    @Override
    public boolean isAvailable() {
        return true;
    }

    @Override
    public boolean install(@NotNull GeneratedPack pack) {
        if (pack.changed() && !pack.isEmpty()) {
            Logger.info("The dialog canvas files are in " + pack.zip() + " (and " + pack.assets() + "): add them to the resource pack of your server");
        }
        return false;
    }

    @Override
    public void requestRebuild() {
    }

    @Override
    public @NotNull String rebuildHint() {
        return "copy the new zmenu-pack.zip into the resource pack of your server";
    }
}
