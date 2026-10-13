package fr.maxlego08.menu.api.utils;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.Inventory;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public record InventoryReplacement(String inventoryName, String plugin, List<Integer> pages, InventoryReplacementMode mode) {
    public static final String DEFAULT_PLUGIN = "zMenu";
    public static final InventoryReplacementMode DEFAULT_MODE = InventoryReplacementMode.CUMULUS;


    public InventoryReplacement(String inventoryName, String plugin, List<Integer> pages) {
        this(inventoryName, plugin, pages, DEFAULT_MODE);
    }

    public InventoryReplacement(String inventoryName, String plugin, List<Integer> pages, InventoryReplacementMode mode) {
        this.inventoryName = inventoryName;
        this.plugin = plugin == null || plugin.equalsIgnoreCase("") ? DEFAULT_PLUGIN : plugin;
        this.pages = pages;
        this.mode = mode;
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

    public void serialize(@NotNull ConfigurationSection section) {
        section.set("name", this.inventoryName);
        if (!DEFAULT_PLUGIN.equals(this.plugin)) section.set("plugin", this.plugin);
        if (!this.pages.isEmpty()) section.set("pages", this.pages);
        if (!DEFAULT_MODE.equals(this.mode)) section.set("mode", this.mode.name());
    }

    @Nullable
    public static InventoryReplacement deserialize(@NotNull ConfigurationSection section) {
        Preconditions.checkNotNull(section, "ConfigurationSection cannot be null");
        String name = section.getString("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        String plugin = section.getString("plugin", DEFAULT_PLUGIN);
        List<Integer> pages = section.getIntegerList("pages");
        InventoryReplacementMode mode = InventoryReplacementMode.valueOf(section.getString("mode", DEFAULT_MODE.name()));
        return new InventoryReplacement(name, plugin, pages, mode);
    }

    public enum InventoryReplacementMode {
        CUMULUS,
        CHEST
    }
}