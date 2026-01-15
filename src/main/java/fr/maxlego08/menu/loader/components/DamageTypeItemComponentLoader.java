package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.DamageTypeComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class DamageTypeItemComponentLoader extends ItemComponentLoader {

    public DamageTypeItemComponentLoader(){
        super("damage_type");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        path = normalizePath(path);
        String damageType = configuration.getString(path);
        if (damageType != null) {
            NamespacedKey key = NamespacedKey.fromString(damageType);
            if (key != null) {
                try {
                    return new DamageTypeComponent(Registry.DAMAGE_TYPE.getOrThrow(key));
                } catch (Exception ignored) {
                }
            }
        }
        return null;
    }
}
