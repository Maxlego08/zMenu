package fr.maxlego08.menu.api;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public interface InventoryOption {

    /**
     * Returns the name of this option.
     * This name is used to identify the option in the configuration file.
     * The name is case-sensitive.
     *
     * @return The name of this option.
     */
    String getName();

    /**
     * Loads the given inventory from the given configuration file
     *
     * @param inventory           the inventory to load
     * @param file                the file that the configuration is loaded from
     * @param configuration       the configuration to load the inventory from
     * @param inventoryManager    the inventory manager to use for inventory operations
     * @param buttonManager       the button manager to use for button-related operations
     */
    void loadInventory(Inventory inventory, File file, YamlConfiguration configuration, InventoryManager inventoryManager, ButtonManager buttonManager);

    /**
     * Writes the keys this option reads in {@link #loadInventory}. Every registered option is asked
     * for every inventory, used or not, so the default writes nothing instead of refusing to serialize.
     *
     * @param inventory The inventory being serialized.
     * @param section   The section of the inventory.
     */
    default void serialize(Inventory inventory, ConfigurationSection section) {
    }

    /**
     * Creates a registered option, with its {@code (Plugin)} constructor or its no-argument one.
     *
     * @return The option, or null if it cannot be created.
     */
    static @Nullable InventoryOption create(@NotNull Plugin plugin, @NotNull Class<? extends InventoryOption> optionClass) {
        try {
            return optionClass.getConstructor(Plugin.class).newInstance(plugin);
        } catch (NoSuchMethodException ignored) {
            try {
                return optionClass.getConstructor().newInstance();
            } catch (ReflectiveOperationException exception) {
                return null;
            }
        } catch (Exception exception) {
            return null;
        }
    }

}
