package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.itemstack.components.PiercingWeaponComponent;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

public class PiercingWeaponItemComponentLoader extends ItemComponentLoader {

    public PiercingWeaponItemComponentLoader(){
        super("piercing_weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        boolean dealsKnockback = componentSection.getBoolean("deals_knockback", true);
        boolean dismounts = componentSection.getBoolean("dismounts", false);
        Optional<Sound> sound = getSound(componentSection.getString("sound"));
        Optional<Sound> hitSound = getSound(componentSection.getString("hit_sound"));
        return new PiercingWeaponComponent(dealsKnockback, dismounts, sound, hitSound);
    }
}
