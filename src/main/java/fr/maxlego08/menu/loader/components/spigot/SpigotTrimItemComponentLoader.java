package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.TrimComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.trim.ArmorTrim;
import org.bukkit.inventory.meta.trim.TrimMaterial;
import org.bukkit.inventory.meta.trim.TrimPattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotTrimItemComponentLoader extends ItemComponentLoader {

    public SpigotTrimItemComponentLoader(){
        super("trim");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String materialString = componentSection.getString("material");
        String patternString = componentSection.getString("pattern");
        if (materialString == null || patternString == null) return null;
        NamespacedKey materialKey = NamespacedKey.fromString(materialString);
        NamespacedKey patternKey = NamespacedKey.fromString(patternString);
        if (materialKey == null || patternKey == null) return null;
        try {
            TrimMaterial trimMaterial = Registry.TRIM_MATERIAL.getOrThrow(materialKey);
            TrimPattern trimPattern = Registry.TRIM_PATTERN.getOrThrow(patternKey);
            return new TrimComponent(new ArmorTrim(trimMaterial, trimPattern));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
