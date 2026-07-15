package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.registry.Registry;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZCustomClickDialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZStaticDialogActionLoader;

public class DialogActionLoaderRegistry extends Registry<String, DialogActionLoader> {

    private static DialogActionLoaderRegistry instance;

    public DialogActionLoaderRegistry() {
        this.registerDefaults();
    }

    public DialogActionLoaderRegistry(boolean registerDefaults) {
        if (registerDefaults) {
            this.registerDefaults();
        }
    }

    private void registerDefaults() {
        this.registerLoader(new ZCustomClickDialogActionLoader());
        this.registerLoader(new ZStaticDialogActionLoader());
    }

    public void registerLoader(DialogActionLoader loader) {
        this.register(loader.getType(), loader);
    }

    public static DialogActionLoaderRegistry getInstance() {
        if (instance == null) {
            instance = new DialogActionLoaderRegistry();
        }
        return instance;
    }
}
