package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DyeColorComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyDyeColorComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.DyedItemColor;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class DyedColorItemComponentLoader extends AbstractColorItemComponentLoader {

    public DyedColorItemComponentLoader(){
        super("dyed-color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);

        Object obj = configuration.get(path);
        if (obj == null) return null;

        ResolvableColor color = this.parseColor(obj);
        if (color == null) return null;

        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new DyeColorComponent(color)
                : new LegacyDyeColorComponent(color);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        DyedItemColor dyedColor = itemStack.getData(DataComponentTypes.DYED_COLOR);
        return dyedColor == null ? null : new DyeColorComponent(ResolvableColor.of(dyedColor.color()));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof LeatherArmorMeta leatherArmorMeta) || !leatherArmorMeta.isDyed()) return null;
        return new LegacyDyeColorComponent(ResolvableColor.of(leatherArmorMeta.getColor()));
    }
}
