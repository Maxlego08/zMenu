package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.registry.RuleLoaderRegistry;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;

public class ZRuleLoaderRegistry extends RuleLoaderRegistry {
    private static ZRuleLoaderRegistry instance;

    public static ZRuleLoaderRegistry getInstance() {
        if (instance == null) {
            instance = new ZRuleLoaderRegistry();
        }
        return instance;
    }

    public void registerDefaultLoaders(@NotNull ZMenuPlugin plugin) {
        ClassRegistry<RuleLoader, MenuPlugin> registry = ClassRegistry
                .<RuleLoader, MenuPlugin>of(RuleLoader.class, this::register)
                .tryNoArgsConstructor()
                .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(pl))
                .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", plugin, AutoRuleLoader.class, registry);

        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto rule loader(s).");
        }

    }
}
