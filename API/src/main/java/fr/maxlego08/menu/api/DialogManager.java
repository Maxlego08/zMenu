package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.exceptions.DialogException;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Collection;
import java.util.Optional;

public interface DialogManager {

    DialogInventory loadDialog(Plugin plugin, String fileName) throws DialogException;

    Optional<DialogInventory> getDialog(String name);

    Optional<DialogInventory> getDialog(String pluginName, String fileName);

    Optional<DialogInventory> getDialog(Plugin plugin, String fileName);

    void deleteDialog(String name);

    void deleteDialog(Plugin plugin);

    void loadDialogs();

    DialogInventory loadInventory(Plugin plugin, String fileName) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, File file) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, String fileName, Class<? extends DialogInventory> dialogClass) throws DialogException, InventoryException;

    DialogInventory loadInventory(Plugin plugin, File file, Class<? extends DialogInventory> dialogClass) throws DialogException, InventoryException;

    //void registerBuilder(DialogBuilder builder);

    void openDialog(Player player, DialogInventory dialog);

    //Optional<DialogBuilder> getDialogBuilder(DialogBodyType type);

    Collection<DialogInventory> getDialogs();

    InventoryManager getInventoryManager();

    void reloadDialogs();
}
