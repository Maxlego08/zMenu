package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.hooks.dialogs.loader.DialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZCustomClickDialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZStaticDialogActionLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DialogActionLoaderRegistry {

    private static DialogActionLoaderRegistry instance;

    private final Map<String, DialogActionLoader> loaders = new HashMap<>();

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
        this.loaders.put(loader.getType(), loader);
    }

    public Optional<DialogActionLoader> get(String type) {
        return Optional.ofNullable(this.loaders.get(type));
    }

    public static DialogActionLoaderRegistry getInstance() {
        if (instance == null) {
            instance = new DialogActionLoaderRegistry();
        }
        return instance;
    }
}
