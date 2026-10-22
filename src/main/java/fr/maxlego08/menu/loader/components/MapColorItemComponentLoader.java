package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.UntilVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.MapColorComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyMapColorComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.MapItemColor;
import org.bukkit.Color;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MapMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@UntilVersion("26.2")
public class MapColorItemComponentLoader extends AbstractColorItemComponentLoader {

    public MapColorItemComponentLoader(){
        super("map-color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        Object o = configuration.get(path);
        if (o == null) return null;
        ResolvableColor resolvableColor = ResolvableColor.of(o);
        if (resolvableColor == null) return null;
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new MapColorComponent(resolvableColor)
                : new LegacyMapColorComponent(resolvableColor);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        MapItemColor mapColor = itemStack.getData(DataComponentTypes.MAP_COLOR);
        return mapColor == null ? null : new MapColorComponent(ResolvableColor.of(mapColor.color()));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof MapMeta mapMeta) || !mapMeta.hasColor()) return null;
        Color color = mapMeta.getColor();
        return color == null ? null : new LegacyMapColorComponent(ResolvableColor.of(color));
    }
}
