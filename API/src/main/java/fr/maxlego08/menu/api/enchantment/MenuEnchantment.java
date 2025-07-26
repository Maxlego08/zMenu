package fr.maxlego08.menu.api.enchantment;

import org.bukkit.enchantments.Enchantment;

import java.util.List;

/**
 * Interface representing a custom enchantment in the plugin.
 * Provides methods to retrieve the underlying enchantment and its associated aliases.
 */
public interface MenuEnchantment {

    /**
     * Retrieves the underlying Bukkit Enchantment associated with this custom enchantment.
     *
     * @return the {@link Enchantment} associated with this custom enchantment
     */
    Enchantment enchantment();

    /**
     * Retrieves the list of aliases for this custom enchantment.
     * Aliases are alternative names that can be used to refer to the enchantment.
     *
     * @return a list of aliases for the enchantment
     */
    List<String> aliases();
}
