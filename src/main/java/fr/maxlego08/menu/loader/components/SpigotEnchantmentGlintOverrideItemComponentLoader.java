package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.EnchantmentGlintOverrideComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotEnchantmentGlintOverrideItemComponentLoader extends ItemComponentLoader {

    public SpigotEnchantmentGlintOverrideItemComponentLoader(){
        super("enchantment_glint_override");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        boolean hasGlint = configuration.getBoolean(path, false);
        return hasGlint ? new EnchantmentGlintOverrideComponent(true) : null;
    }
}
