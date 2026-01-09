package fr.maxlego08.menu.api.event.events;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.MenuEvent;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;

public class InventoryLoadEvent extends MenuEvent {

    private final Plugin plugin;
    private final File file;
    private final Inventory inventory;

    public InventoryLoadEvent(@NotNull Plugin plugin,@NotNull File file,@NotNull Inventory inventory) {
        this.plugin = plugin;
        this.file = file;
        this.inventory = inventory;
    }

    @Contract(pure = true)
    @NotNull
    public Plugin getPlugin() {
        return plugin;
    }

    @Contract(pure = true)
    public File getFile() {
        return file;
    }

    @Contract(pure = true)
    @NotNull
    public Inventory getInventory() {
        return inventory;
    }
}
