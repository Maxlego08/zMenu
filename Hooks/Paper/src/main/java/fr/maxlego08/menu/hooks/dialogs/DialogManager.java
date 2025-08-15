package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.exception.DialogException;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilder;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.BodyLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogActionIntLoader;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.InputLoader;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface DialogManager {

    void load();

    ZDialogs loadDialog(Plugin plugin, String fileName) throws DialogException;

    Optional<ZDialogs> getDialog(String name);

    Optional<ZDialogs> getDialog(String pluginName, String fileName);

    Optional<ZDialogs> getDialog(Plugin plugin, String fileName);

    void deleteDialog(String name);

    void deleteDialog(Plugin plugin);

    void loadDialogs();

    ZDialogs loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException;

    ZDialogs loadInventory(Plugin plugin, File file) throws DialogException, InventoryException;

    ZDialogs loadInventory(Plugin plugin, String fileName, Class<? extends ZDialogs> dialogClass) throws DialogException, InventoryException;

    ZDialogs loadInventory(Plugin plugin, File file, Class<? extends ZDialogs> dialogClass) throws DialogException, InventoryException;

    void registerBodyLoader(BodyLoader bodyLoader);

    void registerInputLoader(InputLoader inputLoader);

    void registerBuilder(DialogBuilder builder);

    void registerActions(DialogActionIntLoader action);

    void openDialog(Player player, String dialogName);

    Optional<BodyLoader> getBodyLoader(String name);

    Optional<InputLoader> getInputLoader(String name);

    Optional<DialogBuilder> getDialogBuilder(DialogBodyType type);

    Optional<DialogActionIntLoader> getDialogAction(String name);

    Collection<ZDialogs> getDialogs();

    InventoryManager getInventoryManager();

    void reloadDialogs();
}
