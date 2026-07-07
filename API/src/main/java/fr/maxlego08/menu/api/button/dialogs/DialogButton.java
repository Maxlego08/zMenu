package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import org.jetbrains.annotations.NotNull;

public abstract class DialogButton<T> extends Button {

    public void onRender(@NotNull DialogRenderContext<T, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {

    }

    public T build(@NotNull DialogRenderContext<T, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        return null;
    }
}
