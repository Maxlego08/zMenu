package fr.maxlego08.menu.api.utils;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.ZMenuKeys;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

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

        if (this.inventoryName == null || !inventory.key().equals(ZMenuKeys.fileKey(this.plugin, this.inventoryName))) {
            return false;
        }

        // Page
        return this.pages.isEmpty() || this.pages.contains(page);
    }

    /**
     * @deprecated use {@link #serialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    public void serialize(@NotNull ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serialize(copy));
    }

    public void serialize(@NotNull Section section) {
        section.set("name", this.inventoryName);
        if (!DEFAULT_PLUGIN.equals(this.plugin)) section.set("plugin", this.plugin);
        if (!this.pages.isEmpty()) section.set("pages", this.pages);
        if (!DEFAULT_MODE.equals(this.mode)) section.set("mode", this.mode.name());
    }

    /**
     * @deprecated use {@link #deserialize(Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    @Nullable
    public static InventoryReplacement deserialize(@NotNull ConfigurationSection section) {
        return deserialize(YamlSections.toDocument(section));
    }

    @Nullable
    public static InventoryReplacement deserialize(@NotNull Section section) {
        Preconditions.checkNotNull(section, "Section cannot be null");
        String name = section.getString("name");
        if (name == null || name.isEmpty()) {
            return null;
        }
        String plugin = section.getString("plugin", DEFAULT_PLUGIN);
        List<Integer> pages = section.getIntList("pages");
        InventoryReplacementMode mode = InventoryReplacementMode.valueOf(section.getString("mode", DEFAULT_MODE.name()));
        return new InventoryReplacement(name, plugin, pages, mode);
    }

    public enum InventoryReplacementMode {
        CUMULUS,
        CHEST
    }
}