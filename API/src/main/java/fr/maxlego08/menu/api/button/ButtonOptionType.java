package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.ZMenuKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

/**
 * A {@link ButtonOption} registered in {@link fr.maxlego08.menu.api.registry.Registries#BUTTON_OPTION}.
 * Every button gets a new instance of every registered option.
 *
 * @param key         the key of the option, {@code <plugin>:<class name>} for {@link #of(Plugin, Class)}
 * @param plugin      the plugin the option belongs to, passed to its constructor
 * @param optionClass the class of the option, created with a {@code (Plugin)} or a no-argument constructor
 */
public record ButtonOptionType(@NotNull Key key, @NotNull Plugin plugin, @NotNull Class<? extends ButtonOption> optionClass) implements Keyed {

    /**
     * Creates the type of an option, keyed by its plugin and the simple name of its class.
     *
     * @param plugin      the plugin the option belongs to
     * @param optionClass the class of the option
     * @return the type
     */
    public static @NotNull ButtonOptionType of(@NotNull Plugin plugin, @NotNull Class<? extends ButtonOption> optionClass) {
        return new ButtonOptionType(ZMenuKeys.fileKey(plugin.getName(), optionClass.getSimpleName()), plugin, optionClass);
    }
}
