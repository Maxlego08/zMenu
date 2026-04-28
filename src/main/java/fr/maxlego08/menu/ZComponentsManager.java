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
import fr.maxlego08.menu.common.factory.VariantItemComponentLoaderFactory;
import fr.maxlego08.menu.common.utils.nms.NmsVersion;
import fr.maxlego08.menu.itemstack.components.paper.PaperVariantComponent;
import fr.maxlego08.menu.itemstack.components.spigot.SpigotVariantComponent;
import fr.maxlego08.menu.loader.components.paper.PaperVariantItemComponentLoader;
import fr.maxlego08.menu.loader.components.spigot.SpigotVariantItemComponentLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.reflections.Reflections;

import java.util.*;

public class ZComponentsManager implements ComponentsManager {
    private final Map<String, ItemComponentLoader> components = new HashMap<>();

    private boolean isPaperAndMiniMessageEnabled(MenuPlugin plugin){
        return plugin.isPaperOrFolia() && Configuration.enableMiniMessageFormat;
    }

    @Override
    public void initializeDefaultComponents(MenuPlugin plugin) {
        MinecraftVersion minecraftVersion = MinecraftVersion.getCurrentVersion();

        Reflections reflection = ReflectionsCache.getInstance().getOrCreate((ZMenuPlugin) plugin, "fr.maxlego08.menu");

        int count = 0;
        Set<Class<?>> typesAnnotatedWith = reflection.getTypesAnnotatedWith(ComponentLoader.class);
        for (Class<?> clazz : typesAnnotatedWith) {
            if (ItemComponentLoader.class.isAssignableFrom(clazz)) {
                if (!VersionFilter.passes(clazz)) continue;

                try {
                    ItemComponentLoader loader;
                    try {
                        loader = (ItemComponentLoader) clazz.getDeclaredConstructor().newInstance();
                    } catch (NoSuchMethodException e) {
                        loader = (ItemComponentLoader) clazz.getDeclaredConstructor(MenuPlugin.class).newInstance(plugin);
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

        Logger.info("Registered <green>" + count + "<reset> default component loaders for Minecraft version <gold>" + minecraftVersion + "<reset>.");


        NmsVersion currentVersion = NmsVersion.getCurrentVersion();
        if (currentVersion.isAttributItemStack()){ // 1.20.5+
            try {
                this.initializeVariantComponents(plugin);
            } catch (Exception e) {
                if (Configuration.enableDebug) {
                    Logger.info("Failed to initialize variant item components:");
                    e.printStackTrace();
                }
            }
        }
    }

    private void initializeVariantComponents(MenuPlugin plugin) {
        NmsVersion currentVersion = NmsVersion.getCurrentVersion();
        VariantItemComponentLoaderFactory loaderFactory =
            plugin.isPaperOrFolia() ? new PaperVariantItemComponentLoader(new PaperVariantComponent())
                             : new SpigotVariantItemComponentLoader(new SpigotVariantComponent());

        this.registerComponent(loaderFactory.getLoaderCatCollar());
        this.registerComponent(loaderFactory.getLoaderCatVariant());
        this.registerComponent(loaderFactory.getLoaderHorse());
        this.registerComponent(loaderFactory.getLoaderRabbit());
        this.registerComponent(loaderFactory.getLoaderSheep());
        this.registerComponent(loaderFactory.getLoaderTropicalFishBaseColor());
        this.registerComponent(loaderFactory.getLoaderTropicalFishPatternColor());
        this.registerComponent(loaderFactory.getLoaderVillager());

        if (currentVersion.isNewMaterial()){ // 1.13+
            this.registerComponent(loaderFactory.getLoaderFox());
            this.registerComponent(loaderFactory.getLoaderMushroomCow());
        }
        if (currentVersion.isNewNMSVersion()){ // 1.17+
            this.registerComponent(loaderFactory.getLoaderAxolotl());
        }
        if (currentVersion.isAttributItemStack()){ // 1.20.5+
            this.registerComponent(loaderFactory.getLoaderWolfCollar());
            this.registerComponent(loaderFactory.getLoaderWolfVariant());
            this.registerComponent(loaderFactory.getLoaderPainting());
        }
        if (currentVersion.is1_21_5OrNewer()){ // 1.21.5+
            try {
                this.registerComponent(loaderFactory.getLoaderChicken());
            } catch (Exception e) {
                if (Configuration.enableDebug){
                    Logger.info("Failed to register Chicken variant component:");
                    e.printStackTrace();
                }
            }
            try {
                this.registerComponent(loaderFactory.getLoaderCow());
            } catch (Exception e) {
                if (Configuration.enableDebug){
                    Logger.info("Failed to register Cow variant component:");
                    e.printStackTrace();
                }
            }
            try {
                this.registerComponent(loaderFactory.getLoaderPig());
            } catch (Exception e) {
                if (Configuration.enableDebug){
                    Logger.info("Failed to register Pig variant component:");
                    e.printStackTrace();
                }
            }
            try {
                this.registerComponent(loaderFactory.getLoaderSalmon());
            } catch (Exception e) {
                if (Configuration.enableDebug){
                    Logger.info("Failed to register Salmon variant component:");
                    e.printStackTrace();
                }
            }
        }
        if (currentVersion.isNewNBTVersion()) { // 1.18+
            this.registerComponent(loaderFactory.getLoaderFrog());
        }
        if (currentVersion.is1_11OrNewer()){ // 1.11+
            this.registerComponent(loaderFactory.getLoaderLlama());
            this.registerComponent(loaderFactory.getLoaderShulkerBox());
        }
        if (currentVersion.is1_12OrNewer()) { // 1.12+
            this.registerComponent(loaderFactory.getLoaderParrot());
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
