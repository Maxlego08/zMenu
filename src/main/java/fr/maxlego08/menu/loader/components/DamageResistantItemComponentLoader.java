package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.DamageResistantComponent;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.Tag;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class DamageResistantItemComponentLoader extends ItemComponentLoader {

    public DamageResistantItemComponentLoader(){
        super("damage_resistant");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if(componentSection == null) return null;
        String damageTypeString = componentSection.getString("types");
        if(damageTypeString == null || damageTypeString.isBlank()) return null;
        if (damageTypeString.startsWith("#")){
            damageTypeString = damageTypeString.substring(1);
        }
        NamespacedKey damageTypeKey = NamespacedKey.fromString(damageTypeString);
        if(damageTypeKey == null) return null;
        Tag<DamageType> damageType = Bukkit.getTag("damage_types", damageTypeKey, DamageType.class);
        if(damageType == null) return null;
        return new DamageResistantComponent(damageType);
    }
}
