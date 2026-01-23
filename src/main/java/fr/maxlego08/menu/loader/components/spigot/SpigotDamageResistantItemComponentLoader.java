package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.DamageResistantComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotDamageResistantItemComponentLoader extends ItemComponentLoader {

    public SpigotDamageResistantItemComponentLoader(){
        super("damage-resistant");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if(componentSection == null) return null;
        String damageTypeString = componentSection.getString("types");
        if(damageTypeString == null || damageTypeString.isBlank()) return null;
        NamespacedKey damageTypeKey = NamespacedKey.fromString(damageTypeString.toLowerCase());
        if(damageTypeKey == null) return null;
        Tag<DamageType> damageType = Bukkit.getTag("damage-types", damageTypeKey, DamageType.class);
        if(damageType == null) return null;
        return new DamageResistantComponent(damageType);
    }
}
