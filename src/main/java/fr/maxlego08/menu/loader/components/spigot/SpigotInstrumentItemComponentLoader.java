package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.InstrumentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableMusicInstrument;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotInstrumentItemComponentLoader extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public SpigotInstrumentItemComponentLoader(MenuPlugin menuPlugin){
        super("instrument");
        this.plugin = menuPlugin;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (!(this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater))
            return null;

        if (componentSection == null) {
            path = this.normalizePath(path);
            String instrument = configuration.getString(path);
            return instrument != null ? new InstrumentComponent(instrument) : null;
        }
        ResolvableComponent description = ResolvableComponent.auto(componentSection.getString("description"), paperMetaUpdater);
        ResolvableFloat useDuration = ResolvableFloat.auto(componentSection.getString("use-duration", "0"));
        ResolvableFloat range = ResolvableFloat.auto(componentSection.getString("range", "0"));

        String soundEvent = componentSection.getString("sound-event");
        ResolvableNamespacedKey resolvableNamespacedKey = ResolvableNamespacedKey.autoOrNull(soundEvent);
        ResolvableSound resolvableSound = ResolvableSound.autoOrNull(soundEvent);
        if (resolvableSound == null) {
            return null;
        }

        return new InstrumentComponent(new ResolvableMusicInstrument(resolvableNamespacedKey, useDuration, range, description, resolvableSound));
    }
}
