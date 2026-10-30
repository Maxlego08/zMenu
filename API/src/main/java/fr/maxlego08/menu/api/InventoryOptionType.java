package fr.maxlego08.menu.api;

import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * An {@link InventoryOption} registered in {@link fr.maxlego08.menu.api.registry.Registries#INVENTORY_OPTION}.
 * Every inventory gets a new instance of every registered option.
 *
 * @param key         the key of the option, {@code <plugin>:<class name>} for {@link #of(Plugin, Class)}
 * @param plugin      the plugin the option belongs to, passed to its constructor
 * @param optionClass the class of the option, created with a {@code (Plugin)} or a no-argument constructor
 */
public record InventoryOptionType(@NotNull Key key, @NotNull Plugin plugin, @NotNull Class<? extends InventoryOption> optionClass) implements Keyed {

    /**
     * Creates the type of an option, keyed by its plugin and the simple name of its class.
     *
     * @param plugin      the plugin the option belongs to
     * @param optionClass the class of the option
     * @return the type
     */
    public static @NotNull InventoryOptionType of(@NotNull Plugin plugin, @NotNull Class<? extends InventoryOption> optionClass) {
        return new InventoryOptionType(ZMenuKeys.fileKey(plugin.getName(), optionClass.getSimpleName()), plugin, optionClass);
    }

    /**
     * Creates a new instance of the option.
     *
     * @return the option, or null if it cannot be created
     */
    public @Nullable InventoryOption create() {
        return InventoryOption.create(this.plugin, this.optionClass);
    }
}
