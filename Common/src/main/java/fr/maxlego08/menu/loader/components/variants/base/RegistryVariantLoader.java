package fr.maxlego08.menu.loader.components.variants.base;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.Keyed;
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

public abstract class RegistryVariantLoader<T extends Keyed> extends ItemComponentLoader {
    private final RegistryKey<T> registryKey;
    private final Function<ResolvableRegistryEntry<T>, ItemComponent> componentFactory;

    protected RegistryVariantLoader(@NotNull String path, @NotNull RegistryKey<T> registryKey, Function<ResolvableRegistryEntry<T>, ItemComponent> componentFactory) {
        super(path);
        this.registryKey = registryKey;
        this.componentFactory = componentFactory;
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        path = this.normalizePath(path);
        ResolvableRegistryEntry<T> resolvableRegistryEntry = ResolvableRegistry.autoOrNull(configuration.getString(path), this.registryKey);
        if (resolvableRegistryEntry == null) return null;
        return this.componentFactory.apply(resolvableRegistryEntry);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    @SuppressWarnings("unchecked")
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Object value = this.getNamedData(itemStack);
        return value instanceof Keyed ? this.componentFactory.apply(ResolvableRegistry.ofValue((T) value, this.registryKey)) : null;
    }
}
