package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.WeaponComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class SpigotWeaponItemComponentLoader extends ItemComponentLoader {

    public SpigotWeaponItemComponentLoader(){
        super("weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        int itemDamagePerAttack = componentSection.getInt("item_damage_per_attack", 1);
        float disableBlockingForSeconds = (float) componentSection.getDouble("disable_blocking_for_seconds", 0);
        return new WeaponComponent(itemDamagePerAttack, disableBlockingForSeconds);
    }
}
