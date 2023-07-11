package fr.maxlego08.menu.api.loader;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

/**
 * Documentation <a href="https://docs.zmenu.dev/api/create-material-loader">here</a>
 * <p>A material loader will allow you to create an ItemStack from the configuration.</p>
 */
public interface MaterialLoader {

    /**
     * Return the key to load the itemStack
     *
     * @return key
     */
	String getKey();

    /**
     * Allows to load an itemStack according to a plugin
     *
     * @param configuration Current configuration
     * @param path Current path
     * @param materialString the material as String
     * @return ItemStack
     */
	ItemStack load(YamlConfiguration configuration, String path, String materialString);

}
