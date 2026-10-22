package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.ZMenuKeys;

import java.util.List;

public record InventoryReplacement(String inventoryName, String plugin, List<Integer> pages) {
    public InventoryReplacement(String inventoryName, String plugin, List<Integer> pages) {
        this.inventoryName = inventoryName;
        this.plugin = plugin == null || plugin.equalsIgnoreCase("") ? "zMenu" : plugin;
        this.pages = pages;
    }

    public boolean shouldTrigger(Inventory inventory, int page) {

        if (this.inventoryName == null || !inventory.key().equals(ZMenuKeys.fileKey(this.plugin, this.inventoryName))) {
            return false;
        }

        // Page
        return this.pages.isEmpty() || this.pages.contains(page);
    }
}