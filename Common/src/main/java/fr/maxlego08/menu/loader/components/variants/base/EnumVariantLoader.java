package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
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

public abstract class EnumVariantLoader<T extends Enum<T>> extends ItemComponentLoader {
    private final Class<T> enumClass;
    private final Function<ResolvableEnum<T>, ItemComponent> componentFactory;

    protected EnumVariantLoader(String path, Class<T> enumClass, Function<ResolvableEnum<T>, ItemComponent> componentFactory) {
        super(path);
        this.enumClass = enumClass;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        ResolvableEnum<T> resolvableEnum = ResolvableEnum.autoOrNull(this.enumClass, value);
        return resolvableEnum != null ? this.componentFactory.apply(resolvableEnum) : null;
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
        Object value = this.getNamedData(itemStack);
        return this.enumClass.isInstance(value) ? this.componentFactory.apply(ResolvableEnum.of(this.enumClass, this.enumClass.cast(value))) : null;
    }
}
