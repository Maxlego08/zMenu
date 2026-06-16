package fr.maxlego08.menu.api.enchantment;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;

/**
 * Interface for managing custom enchantments in the plugin.
 * Provides methods to retrieve, register, and list available enchantments.
 */
public interface Enchantments {

    /**
     * Retrieves an enchantment by its name.
     *
     * @param enchantment the name of the enchantment
     * @return an {@link Optional} containing the enchantment if found, otherwise empty
     */
    @Contract(pure = true)
    @NotNull
    Optional<MenuEnchantment> getEnchantments(@NotNull String enchantment);

    /**
     * Registers all custom enchantments within the plugin.
     */
    void register();

    /**
     * Retrieves a list of all registered enchantments' names.
     *
     * @return a list of enchantment names
     */
    @Contract(pure = true)
    @NotNull
    List<String> getEnchantments();
}
