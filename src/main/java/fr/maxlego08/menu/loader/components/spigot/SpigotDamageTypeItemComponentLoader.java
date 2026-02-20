package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DamageTypeComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotDamageTypeItemComponentLoader extends ItemComponentLoader {

    public SpigotDamageTypeItemComponentLoader(){
        super("damage-type");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        String damageType = componentSection.getString("types");
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
