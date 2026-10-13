package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.InstrumentComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyInstrumentComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableComponent;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableMusicInstrument;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.MusicInstrument;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class InstrumentItemComponentLoader extends ItemComponentLoader {
    private final MenuPlugin plugin;

    public InstrumentItemComponentLoader(MenuPlugin menuPlugin){
        super("instrument");
        this.plugin = menuPlugin;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (!(this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater))
            return null;

        boolean dataComponents = MinecraftVersion.isServerAtLeast("1.21.3");
        if (componentSection == null) {
            path = this.normalizePath(path);
            String instrument = configuration.getString(path);
            if (instrument == null) return null;
            return dataComponents ? new InstrumentComponent(instrument) : new LegacyInstrumentComponent(instrument);
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

        ResolvableMusicInstrument instrument = new ResolvableMusicInstrument(resolvableNamespacedKey, useDuration, range, description, resolvableSound);
        return dataComponents ? new InstrumentComponent(instrument) : new LegacyInstrumentComponent(instrument);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        if (!(this.plugin.getMetaUpdater() instanceof PaperMetaUpdater)) return null;
        MusicInstrument instrument = itemStack.getData(DataComponentTypes.INSTRUMENT);
        if (instrument == null) return null;
        NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKey(instrument);
        return key == null ? null : new InstrumentComponent(key.asString());
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(this.plugin.getMetaUpdater() instanceof PaperMetaUpdater)) return null;
        if (!(itemStack.getItemMeta() instanceof MusicInstrumentMeta musicInstrumentMeta)) return null;
        MusicInstrument instrument = musicInstrumentMeta.getInstrument();
        if (instrument == null) return null;
        NamespacedKey key = RegistryAccess.registryAccess().getRegistry(RegistryKey.INSTRUMENT).getKey(instrument);
        return key == null ? null : new LegacyInstrumentComponent(key.asString());
    }
}
