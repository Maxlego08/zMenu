package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.loader.DialogActionLoader;
import fr.maxlego08.menu.api.registry.RegistryManager;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZCustomClickDialogActionLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.dialogaction.ZStaticDialogActionLoader;
import fr.maxlego08.menu.registry.ZRegistryManager;

import java.lang.reflect.Proxy;

/**
 * Builds the dialog action registry the way {@code ZDialogManager} fills it when the plugin enables.
 */
public final class DialogActionRegistries {

    private DialogActionRegistries() {
    }

    public static RegistryManager create() {
        RegistryManager registries = new ZRegistryManager();
        WritableRegistry<DialogActionLoader> loaders = registries.writable(Registries.DIALOG_ACTION);
        loaders.register("zmenu", new ZStaticDialogActionLoader());
        loaders.register("zmenu", new ZCustomClickDialogActionLoader());
        return registries;
    }

    public static MenuPlugin plugin(RegistryManager registries) {
        InventoryManager inventoryManager = (InventoryManager) Proxy.newProxyInstance(InventoryManager.class.getClassLoader(), new Class<?>[]{InventoryManager.class},
                (instance, method, arguments) -> method.getName().equals("getRegistries") ? registries : null);
        return (MenuPlugin) Proxy.newProxyInstance(MenuPlugin.class.getClassLoader(), new Class<?>[]{MenuPlugin.class},
                (instance, method, arguments) -> method.getName().equals("getInventoryManager") ? inventoryManager : null);
    }
}
