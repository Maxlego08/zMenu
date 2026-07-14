package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ComponentsManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        ClassRegistry<ItemComponentLoader,MenuPlugin> registry =
                ClassRegistry.<ItemComponentLoader, MenuPlugin>of(ItemComponentLoader.class, this::registerComponent)
                        .tryNoArgsConstructor()
                        .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(pl))
                        .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", plugin, AutoComponentLoader.class, registry);

        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto component loader(s).");
        }
    }

    @Override
    public void registerComponent(@NotNull ItemComponentLoader loader) throws ItemComponentAlreadyRegisterException {
        List<String> componentNames = loader.getComponentNames();
        for (String name : componentNames) {
            if (this.components.containsKey(name)) {
                throw new ItemComponentAlreadyRegisterException("Component with name '" + name + "' is already registered.");
            }
            this.components.put(name, loader);
        }
    }

    public @NotNull Set<String> getRegisteredComponentNames() {
        return Collections.unmodifiableSet(this.components.keySet());
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        return Optional.ofNullable(this.components.get(name));
    }
}
