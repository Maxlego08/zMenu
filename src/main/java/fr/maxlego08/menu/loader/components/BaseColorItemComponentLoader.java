package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.BaseColorComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyBaseColorComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.DyeColor;
import org.bukkit.block.Banner;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BlockStateMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class BaseColorItemComponentLoader extends ItemComponentLoader {

    public BaseColorItemComponentLoader(){
        super("base-color");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        String string = configuration.getString(path);
        if (string == null) return null;
        Resolvable<DyeColor> dyeColorResolvable;
        try {
            DyeColor baseColor = DyeColor.valueOf(string.toUpperCase(Locale.ROOT));
            dyeColorResolvable = SimpleResolvable.of(baseColor, DyeColor::valueOf);
        } catch (IllegalArgumentException e) {
            dyeColorResolvable = SimpleResolvable.ofExpression(string, s -> {
                String normalized = s.trim()
                        .replace(" ", "_")
                        .replace("-", "_")
                        .toUpperCase(Locale.ROOT);
                return Enum.valueOf(DyeColor.class, normalized);
            });
        }

        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new BaseColorComponent(dyeColorResolvable)
                : new LegacyBaseColorComponent(dyeColorResolvable);
    }

    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        DyeColor baseColor = itemStack.getData(DataComponentTypes.BASE_COLOR);
        return baseColor == null ? null : new BaseColorComponent(SimpleResolvable.of(baseColor, DyeColor::valueOf));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        DyeColor baseColor = readBaseColor(itemStack.getItemMeta());
        return baseColor == null ? null : new LegacyBaseColorComponent(SimpleResolvable.of(baseColor, DyeColor::valueOf));
    }

    private static @Nullable DyeColor readBaseColor(@Nullable ItemMeta itemMeta) {
        if (!(itemMeta instanceof BlockStateMeta blockStateMeta) || !blockStateMeta.hasBlockState()) return null;
        return blockStateMeta.getBlockState() instanceof Banner banner ? banner.getBaseColor() : null;
    }
}
