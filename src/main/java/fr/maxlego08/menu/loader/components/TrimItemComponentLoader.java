package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.TrimComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyTrimComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableArmorTrim;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemArmorTrim;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ArmorMeta;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
public class TrimItemComponentLoader extends ItemComponentLoader {

    public TrimItemComponentLoader() {
        super("trim");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String materialString = componentSection.getString("material");
        String patternString = componentSection.getString("pattern");
        ResolvableRegistryEntry<TrimMaterial> trimMaterialRegistryEntry = ResolvableRegistry.autoOrNull(materialString, RegistryKey.TRIM_MATERIAL);
        ResolvableRegistryEntry<TrimPattern> trimPatternRegistryEntry = ResolvableRegistry.autoOrNull(patternString, RegistryKey.TRIM_PATTERN);
        if (trimMaterialRegistryEntry == null || trimPatternRegistryEntry == null) return null;
        ResolvableArmorTrim armorTrim = new ResolvableArmorTrim(trimMaterialRegistryEntry, trimPatternRegistryEntry);
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new TrimComponent(armorTrim)
                : new LegacyTrimComponent(armorTrim);
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        ItemArmorTrim trim = itemStack.getData(DataComponentTypes.TRIM);
        if (trim == null) return null;
        ArmorTrim armorTrim = trim.armorTrim();
        ResolvableRegistryEntry<TrimMaterial> material = ResolvableRegistry.ofRegisteredOrNull(armorTrim.getMaterial(), RegistryKey.TRIM_MATERIAL);
        ResolvableRegistryEntry<TrimPattern> pattern = ResolvableRegistry.ofRegisteredOrNull(armorTrim.getPattern(), RegistryKey.TRIM_PATTERN);
        if (material == null || pattern == null) return null;
        return new TrimComponent(new ResolvableArmorTrim(material, pattern));
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof ArmorMeta armorMeta) || !armorMeta.hasTrim()) return null;
        ArmorTrim armorTrim = armorMeta.getTrim();
        if (armorTrim == null) return null;
        ResolvableRegistryEntry<TrimMaterial> material = ResolvableRegistry.ofRegisteredOrNull(armorTrim.getMaterial(), RegistryKey.TRIM_MATERIAL);
        ResolvableRegistryEntry<TrimPattern> pattern = ResolvableRegistry.ofRegisteredOrNull(armorTrim.getPattern(), RegistryKey.TRIM_PATTERN);
        if (material == null || pattern == null) return null;
        return new LegacyTrimComponent(new ResolvableArmorTrim(material, pattern));
    }
}
