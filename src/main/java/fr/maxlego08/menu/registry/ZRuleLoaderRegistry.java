package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoRuleLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.registry.RuleLoaderRegistry;
import fr.maxlego08.menu.api.rules.loader.RuleLoader;
import fr.maxlego08.menu.api.utils.ReflectionsCache;
import fr.maxlego08.menu.test.common.VersionFilter;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.Set;

public class ZRuleLoaderRegistry extends RuleLoaderRegistry {
    private static ZRuleLoaderRegistry instance;

    public static ZRuleLoaderRegistry getInstance() {
        if (instance == null) {
            instance = new ZRuleLoaderRegistry();
        }
        return instance;
    }

    public void registerDefaultLoaders(@NotNull ZMenuPlugin plugin) {
        Reflections reflection = ReflectionsCache.getInstance().getOrCreate(plugin, "fr.maxlego08.menu");

        int count = 0;
        Set<Class<?>> typesAnnotatedWith = reflection.getTypesAnnotatedWith(AutoRuleLoader.class);
        for (Class<?> clazz : typesAnnotatedWith) {
            if (!RuleLoader.class.isAssignableFrom(clazz)) {
                Logger.info("Class " + clazz.getName() + " is annotated with @AutoRuleLoader but does not implement RuleLoader.");
                continue;
            }
            if (!VersionFilter.passes(clazz)) continue;
            try {
                RuleLoader loader = (RuleLoader) clazz.getDeclaredConstructor().newInstance();
                register(loader);
                count++;
            } catch (Exception e) {
                Logger.info("Failed to register rule loader: " + clazz.getName());
                e.printStackTrace();
            }
        }

        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " rule loaders.");
        }
    }
}
