package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.ProvidesTrimMaterialComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.5")
public class ProvidesTrimMaterialItemComponentLoader extends ItemComponentLoader {

    public ProvidesTrimMaterialItemComponentLoader(){
        super("provides-trim-material");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = this.normalizePath(path);
        String value = configuration.getString(path);
        ResolvableRegistryEntry<TrimMaterial> trimMaterialResolvableRegistryEntry = ResolvableRegistry.autoOrNull(value, RegistryKey.TRIM_MATERIAL);
        return new ProvidesTrimMaterialComponent(trimMaterialResolvableRegistryEntry);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        TrimMaterial trimMaterial = itemStack.getData(DataComponentTypes.PROVIDES_TRIM_MATERIAL);
        if (trimMaterial == null) return null;
        ResolvableRegistryEntry<TrimMaterial> material = ResolvableRegistry.ofRegisteredOrNull(trimMaterial, RegistryKey.TRIM_MATERIAL);
        return material == null ? null : new ProvidesTrimMaterialComponent(material);
    }
}
