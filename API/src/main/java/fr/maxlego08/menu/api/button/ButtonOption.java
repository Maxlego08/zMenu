package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.io.File;
import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

/**
 * Represents a button option within a plugin's user interface.
 */
public interface ButtonOption {

    /**
     * Gets the name of the button option.
     *
     * @return The name of this button option.
     */
    String getName();

    /**
     * Gets the plugin instance associated with this button option.
     *
     * @return The plugin instance.
     */
    Plugin getPlugin();

    /**
     * Loads a button configuration from a YAML file.
     *
     * This method is responsible for initializing a button with specific settings
     * defined in a YAML configuration file. It utilizes an {@link InventoryManager},
     * a {@link ButtonManager}, and a {@link Loader} for {@link MenuItemStack} to
     * fully configure the button.
     *
     * @param button The button to configure.
     * @param configuration The YAML configuration containing the button settings.
     * @param path The path within the YAML file where the button's settings are defined.
     * @param inventoryManager The inventory manager to use for inventory operations.
     * @param buttonManager The button manager to use for button-related operations.
     * @param itemStackLoader The loader to use for loading {@link MenuItemStack} instances.
     * @param file The file from which the configuration is loaded.
     * @deprecated use {@link #loadButton(Button, YamlDocument, String, InventoryManager, ButtonManager, Loader, File)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    void loadButton(Button button, YamlConfiguration configuration, String path, InventoryManager inventoryManager, ButtonManager buttonManager, Loader<MenuItemStack> itemStackLoader, File file);

    /**
     * Configures a button from its document. zMenu calls this method, override it.
     * By default it reads a Bukkit copy with the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    default void loadButton(Button button, YamlDocument configuration, String path, InventoryManager inventoryManager, ButtonManager buttonManager, Loader<MenuItemStack> itemStackLoader, File file) {
        this.loadButton(button, YamlSections.toBukkit(configuration), path, inventoryManager, buttonManager, itemStackLoader, file);
    }

    /**
     * Handles a click event on a button by a player.
     *
     * This method is called when a player clicks a button. It allows for custom
     * actions to be executed based on the button clicked, the player who clicked it,
     * and the context in which the click occurred.
     *
     * @param button The button that was clicked.
     * @param player The player who clicked the button.
     * @param event The click event that occurred.
     * @param inventoryEngine The inventory in which the click occurred.
     * @param slot The slot number where the click occurred.
     * @param isSuccess Indicates whether the action associated with the button was successful.
     */
    void onClick(Button button, Player player, InventoryClickEvent event, InventoryEngine inventoryEngine, int slot, boolean isSuccess);

    /**
     * Writes the keys this option reads in {@link #loadButton}. Every button holds every registered
     * option, used or not, so the default writes nothing instead of refusing to serialize.
     *
     * @param button  The button being serialized.
     * @param section The section of the button.
     * @deprecated use {@link #serialize(Button, Section)}, zMenu reads its files with BoostedYAML.
     */
    @Deprecated(since = "1.1.2.1", forRemoval = true)
    default void serialize(Button button, ConfigurationSection section) {
    }

    /**
     * Writes the keys this option reads. zMenu calls this method, override it.
     * By default it writes through the deprecated method, which existing addons override.
     * <p>BoostedYAML is loaded by zMenu: an addon that calls this method must also declare it.
     */
    default void serialize(Button button, Section section) {
        YamlSections.write(section, copy -> this.serialize(button, copy));
    }
}

