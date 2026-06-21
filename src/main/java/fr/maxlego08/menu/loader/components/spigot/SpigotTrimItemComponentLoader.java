package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.TrimComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.RegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableArmorTrim;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotTrimItemComponentLoader extends ItemComponentLoader {

    public SpigotTrimItemComponentLoader() {
        super("trim");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String materialString = componentSection.getString("material");
        String patternString = componentSection.getString("pattern");
//         NamespacedKey materialKey = NamespacedKey.fromString(materialString);
//         NamespacedKey patternKey = NamespacedKey.fromString(patternString);
//         if (materialKey == null || patternKey == null) return null;
//         try {
//             TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.getOrThrow(materialKey);
//             TrimPattern trimPattern = Registry.TRIM_PATTERN.getOrThrow(patternKey);
//             return new TrimComponent(new ArmorTrim(trimMaterial, trimPattern));
//         } catch (IllegalArgumentException e) {
//             return null;
//         }
        RegistryEntry<TrimMaterial> trimMaterialRegistryEntry = ResolvableRegistry.autoOrNull(materialString, TrimMaterial.class);
        RegistryEntry<TrimPattern> trimPatternRegistryEntry = ResolvableRegistry.autoOrNull(patternString, TrimPattern.class);
        if (trimMaterialRegistryEntry == null || trimPatternRegistryEntry == null) return null;
        return new TrimComponent(new ResolvableArmorTrim(trimMaterialRegistryEntry, trimPatternRegistryEntry));
    }
}
