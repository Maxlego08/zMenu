package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.Inventory;

import java.util.List;
import java.util.Objects;

public class InventoryReplacement {
    private final String inventoryName;
    private final String plugin;
    private final List<Integer> pages;

    public InventoryReplacement(String inventoryName, String plugin, List<Integer> pages) {
        this.inventoryName = inventoryName;
        this.plugin = plugin == null || plugin.equalsIgnoreCase("") ? "zMenu" : plugin;
        this.pages = pages;
    }

    public String getInventoryName() {
        return this.inventoryName;
    }

    public String getPlugin() {
        return this.plugin;
    }

    public List<Integer> getPages() {
        return this.pages;
    }

    public boolean shouldTrigger(Inventory inventory, int page) {

        // Inventory Name
        if (!Objects.equals(this.inventoryName, inventory.getFileName())) {
            return false;
        }
        // Plugin Name
        if (!Objects.equals(this.plugin, inventory.getPlugin().getName())) {
            return false;
        }

        // Page
        return this.pages.isEmpty() || this.pages.contains(page);
    }
}