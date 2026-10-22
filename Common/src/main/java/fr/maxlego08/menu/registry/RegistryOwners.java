package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.registry.RegistrationException;
import fr.maxlego08.menu.api.registry.WritableRegistry;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Registers entries handed over through the methods that existed before the registries, which
 * take no owner.
 */
public final class RegistryOwners {

    /**
     * The owner of what no plugin class loader provides, such as zMenu's own entries in tests.
     */
    public static final String FALLBACK = "zMenu";

    private RegistryOwners() {
    }

    /**
     * Finds the plugin an entry belongs to: the one whose class loader loaded its class.
     *
     * @param value the entry
     * @return the name of that plugin, or {@link #FALLBACK}
     */
    public static @NotNull String of(@NotNull Object value) {
        try {
            return JavaPlugin.getProvidingPlugin(value.getClass()).getName();
        } catch (IllegalArgumentException | IllegalStateException | ClassCastException exception) {
            return FALLBACK;
        }
    }

    /**
     * Registers an entry under its key and aliases, owned by the plugin that provides it. A clash is
     * logged instead of thrown, as the old register methods never failed the plugin calling them.
     *
     * @param registry the registry
     * @param value    the entry
     * @param aliases  the other names of the entry
     * @return whether the entry was registered
     */
    public static <T extends Keyed> boolean registerLegacy(@NotNull WritableRegistry<T> registry, @NotNull T value, @NotNull Collection<Key> aliases) {
        try {
            registry.register(of(value), value, aliases);
            return true;
        } catch (RegistrationException exception) {
            Logger.info(exception.getMessage(), Logger.LogType.WARNING);
            return false;
        }
    }
}
