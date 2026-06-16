package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ComponentsManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ClassRegistry;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.version.VersionFilter;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperVariantComponent;
import fr.maxlego08.menu.itemstack.components.spigot.SpigotVariantComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        VariantComponent variantComponent = plugin.isPaperOrFolia() ? new PaperVariantComponent() : new SpigotVariantComponent();
        ClassRegistry<ItemComponentLoader,MenuPlugin> registry =
                ClassRegistry.<ItemComponentLoader, MenuPlugin>of(ItemComponentLoader.class, this::registerComponent)
                        .tryNoArgsConstructor()
                        .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(pl))
                        .tryConstructor((clazz, pl) -> clazz.getDeclaredConstructor(MenuPlugin.class, VariantComponent.class).newInstance(pl, variantComponent))
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

    @Override
    public @NotNull Optional<ItemComponentLoader> getLoader(@NotNull String name) {
        return Optional.ofNullable(this.components.get(name));
    }
}
