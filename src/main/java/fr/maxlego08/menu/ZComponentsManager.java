package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ComponentsManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.registry.KeyedRegistry;
import fr.maxlego08.menu.api.registry.Registries;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.registry.RegistryOwners;
import fr.maxlego08.menu.registry.ZRegistryManager;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class ZComponentsManager implements ComponentsManager {

    private final ZRegistryManager registries;

    public ZComponentsManager(@NotNull ZRegistryManager registries) {
        this.registries = registries;
    }

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        ClassRegistry<ItemComponentLoader,MenuPlugin> registry =
                ClassRegistry.<ItemComponentLoader, MenuPlugin>of(ItemComponentLoader.class, loader -> this.registries.writable(Registries.ITEM_COMPONENT).register(plugin, loader, loader.aliases()))
                        .tryNoArgsConstructor()
                        .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(pl))
                        .errorLogger(Logger::error);

        int count = VersionFilter.scanAndRegister("fr.maxlego08.menu", plugin, AutoComponentLoader.class, registry);

        if (Configuration.enableInformationMessage) {
            Logger.info("Registered " + count + " auto component loader(s).");
        }
    }

    @Override
    public void registerComponent(@NotNull ItemComponentLoader loader) {
        RegistryOwners.registerLegacy(this.registries.writable(Registries.ITEM_COMPONENT), loader, loader.aliases());
    }

    /**
     * @return every name a component can be written with, as each component declares them
     */
    public @NotNull Set<String> getRegisteredComponentNames() {
        Set<String> names = new HashSet<>();
        for (ItemComponentLoader loader : this.components()) {
            names.addAll(loader.getComponentNames());
        }
        return Collections.unmodifiableSet(names);
    }

    @Override
    public @NotNull Collection<ItemComponentLoader> getLoaders() {
        return this.components().values();
    }

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        Key key = ZMenuKeys.tryParse(name);
        return key == null ? Optional.empty() : this.components().get(key);
    }

    private KeyedRegistry<ItemComponentLoader> components() {
        return this.registries.registry(Registries.ITEM_COMPONENT);
    }
}
