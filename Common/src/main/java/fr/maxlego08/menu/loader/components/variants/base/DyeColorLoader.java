package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDyeColor;
import fr.maxlego08.menu.loader.components.AbstractColorItemComponentLoader;
import org.bukkit.DyeColor;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.function.Function;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public abstract class DyeColorLoader extends AbstractColorItemComponentLoader {
    private final Function<ResolvableDyeColor, ItemComponent> componentFactory;

    protected DyeColorLoader(String path, Function<ResolvableDyeColor, ItemComponent> componentFactory) {
        super(path);
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        ResolvableDyeColor resolvableDyeColor = ResolvableDyeColor.autoOrNull(configuration.get(path));
        if (resolvableDyeColor == null) return null;
        return this.componentFactory.apply(resolvableDyeColor);
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
        return this.getNamedData(itemStack) instanceof DyeColor dyeColor ? this.componentFactory.apply(ResolvableDyeColor.of(dyeColor)) : null;
    }
}
