package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.zcore.utils.itemstack.ZKineticWeaponCondition;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.meta.components.KineticWeaponComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Optional;

public class SpigotKineticWeaponItemComponentLoader extends ItemComponentLoader {

    public SpigotKineticWeaponItemComponentLoader(){
        super("kinetic_weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        int delayTicks = componentSection.getInt("delay_ticks", 0);
        Optional<KineticWeaponComponent.Condition> damageConditions = getCondition(componentSection.getConfigurationSection("damage_conditions"));
        Optional<KineticWeaponComponent.Condition> dismountConditions = getCondition(componentSection.getConfigurationSection("dismount_conditions"));
        Optional<KineticWeaponComponent.Condition> knockbackConditions = getCondition(componentSection.getConfigurationSection("knockback_conditions"));
        float forwardMovement = (float) componentSection.getDouble("forward_movement", 0d);
        float damageMultiplier = (float) componentSection.getDouble("damage_multiplier", 1d);
        Optional<Sound> sound = getSound(componentSection.getString("sound", ""));
        Optional<Sound> hitSound = getSound(componentSection.getString("hit_sound", ""));
        return new fr.maxlego08.menu.itemstack.components.KineticWeaponComponent(delayTicks,
                damageConditions,
                dismountConditions,
                knockbackConditions,
                forwardMovement,
                damageMultiplier,
                sound,
                hitSound
        );
    }

    private Optional<KineticWeaponComponent.Condition> getCondition(@Nullable ConfigurationSection configurationSection){
        if (configurationSection == null) return Optional.empty();
        int maxDurationTicks = configurationSection.getInt("max_duration_ticks", -1);
        float minSpeed = (float) configurationSection.getDouble("min_speed", 0d);
        float minRelativeSpeed = (float) configurationSection.getDouble("min_relative_speed", 0d);
        if (maxDurationTicks < 0) return Optional.empty();
        return Optional.of(new ZKineticWeaponCondition(maxDurationTicks, minSpeed, minRelativeSpeed));
    }
}
