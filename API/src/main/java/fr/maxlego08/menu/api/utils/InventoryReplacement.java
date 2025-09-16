package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;

import java.util.List;
import java.util.Objects;


/**
 * <p>Represents the item that can be interacted with to open a menu.</p>
 */
public class InventoryReplacement {
    private final String inventoryName;
    private final String plugin;
    private final List<Integer> pages;

    public InventoryReplacement(String inventoryName, String plugin, List<Integer> pages) {
        this.inventoryName = inventoryName;
        this.plugin = plugin;
        this.pages = pages;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public String getPlugin() {
        return plugin;
    }

    public List<Integer> getPages() {
        return pages;
    }

    public boolean shouldTrigger(PlayerOpenInventoryEvent event) {
        // Inventory Name
        if (!Objects.equals(this.inventoryName, event.getInventory().getName())) {
            return false;
        }

        // Plugin Name
        if (this.plugin != null && !Objects.equals(this.plugin, event.getInventory().getPlugin().getName())) {
            return false;
        }

        // Page
        return this.pages == null || this.pages.contains(event.getPage());
    }
}