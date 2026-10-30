package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.rules.Rule;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;
import java.util.TreeSet;

/**
 * Reads item rules with the rule types of {@link Registries#RULE}.
 */
public final class ZRuleLoaderRegistry {

    private ZRuleLoaderRegistry() {
    }

    /**
     * Registers zMenu's own rule types, found by their {@link AutoRuleLoader} annotation.
     *
     * @param plugin zMenu
     */
    public static void registerDefaultLoaders(@NotNull ZMenuPlugin plugin) {
        ZRegistryManager registries = plugin.getInventoryManager().getRegistries();
        ClassRegistry<RuleLoader, MenuPlugin> registry = ClassRegistry
                .<RuleLoader, MenuPlugin>of(RuleLoader.class, loader -> registries.writable(Registries.RULE).register(plugin, loader, loader.aliases()))
                .tryNoArgsConstructor()
                .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(pl))
                .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", plugin, AutoRuleLoader.class, registry);

        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto rule loader(s).");
        }
    }

    /**
     * Reads a rule from its configuration, whose {@code type} names a rule type.
     *
     * @param configuration the configuration of the rule
     * @return the rule, or null if its type is missing or unknown
     */
    public static @Nullable Rule loadRule(@NotNull Map<String, Object> configuration) {
        Object type = configuration.get("type");
        if (type == null) {
            return null;
        }

        KeyedRegistry<RuleLoader> registry = ZMenuPlugin.getInstance().getInventoryManager().getRegistries().registry(Registries.RULE);
        Key key = ZMenuKeys.tryParse(type.toString());
        Optional<RuleLoader> ruleLoader = key == null ? Optional.empty() : registry.get(key);
        if (ruleLoader.isEmpty()) {
            TreeSet<String> names = new TreeSet<>();
            registry.keys().forEach(id -> names.add(id.value()));
            registry.aliases().keySet().forEach(id -> names.add(id.value()));
            Logger.info("Unknown rule type: " + type, "Available types: " + names);
            return null;
        }
        return ruleLoader.get().load(configuration);
    }
}
