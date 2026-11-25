package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MapIdComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyMapIdComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapId;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class MapIdItemComponentLoader extends ItemComponentLoader {

    public MapIdItemComponentLoader(){
        super("map-id");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        ResolvableInt mapId = this.asResolvableInt(configuration, path);
        if (mapId == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new MapIdComponent(mapId)
                : new LegacyMapIdComponent(mapId);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        MapId mapId = itemStack.getData(DataComponentTypes.MAP_ID);
        return mapId == null ? null : new MapIdComponent(ResolvableInt.of(mapId.id()));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof MapMeta mapMeta) || !mapMeta.hasMapId()) return null;
        return new LegacyMapIdComponent(ResolvableInt.of(mapMeta.getMapId()));
    }
}
