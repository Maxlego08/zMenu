package fr.maxlego08.menu;

import fr.maxlego08.menu.api.ComponentsManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.ComponentLoader;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.exceptions.ItemComponentAlreadyRegisterException;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.ReflectionsCache;
import fr.maxlego08.menu.common.MinecraftVersion;
import fr.maxlego08.menu.common.VersionFilter;
import fr.maxlego08.menu.common.interfaces.VariantComponent;
import fr.maxlego08.menu.itemstack.components.paper.PaperVariantComponent;
import fr.maxlego08.menu.itemstack.components.spigot.SpigotVariantComponent;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        MinecraftVersion minecraftVersion = MinecraftVersion.getCurrentVersion();

        Reflections reflection = ReflectionsCache.getInstance().getOrCreate((ZMenuPlugin) plugin, "fr.maxlego08.menu");
        VariantComponent variantComponent = plugin.isPaperOrFolia() ? new PaperVariantComponent() : new SpigotVariantComponent();

        int count = 0;
        Set<Class<?>> typesAnnotatedWith = reflection.getTypesAnnotatedWith(ComponentLoader.class);
        for (Class<?> clazz : typesAnnotatedWith) {
            if (ItemComponentLoader.class.isAssignableFrom(clazz)) {
                if (!VersionFilter.passes(clazz)) continue;

                try {
                    ItemComponentLoader loader;
                    try {
                        loader = (ItemComponentLoader) clazz.getDeclaredConstructor(MenuPlugin.class, VariantComponent.class).newInstance(plugin, variantComponent);
                    } catch (NoSuchMethodException e1) {
                        try {
                            loader = (ItemComponentLoader) clazz.getDeclaredConstructor().newInstance();
                        } catch (NoSuchMethodException e2) {
                            loader = (ItemComponentLoader) clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(plugin);
                        }
                    }
                    this.registerComponent(loader);
                    count++;
                } catch (Exception e) {
                    if (Configuration.enableDebug) {
                        Logger.info("Failed to instantiate component loader: " + clazz.getName());
                        e.printStackTrace();
                    }
                }
            }
        }

        Logger.info("Registered " + count + " default component loaders for Minecraft version " + minecraftVersion + ".");
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
