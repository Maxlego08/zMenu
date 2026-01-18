package fr.maxlego08.menu.api.interfaces;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Interface for classes that load custom item components from configuration.
 * Used to decouple loading implementations from main logic.
 */
public interface ItemComponentLoaderInterface {
    @Nullable
    /**
     * Loads a custom item component from configuration.
     *
     * @param file              The file being read.
     * @param configuration     The parsed configuration data.
     * @param path              Path inside the config for this component.
     * @param componentSection  Section in configuration for this component (may be null).
     * @return An ItemComponent, or null if not applicable.
     */
    ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection);
}
