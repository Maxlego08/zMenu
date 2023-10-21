package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.MenuEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;

public class InventoryLoadEvent extends MenuEvent {

    private final Plugin plugin;
    private final File file;
    private final Inventory inventory;

    public InventoryLoadEvent(Plugin plugin, File file, Inventory inventory) {
        this.plugin = plugin;
        this.file = file;
        this.inventory = inventory;
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return file;
    }

    public Inventory getInventory() {
        return inventory;
    }
}
