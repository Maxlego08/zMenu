package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.KineticWeaponComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableKineticCondition;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public final class KineticWeaponItemComponentLoader extends ItemComponentLoader {

    public KineticWeaponItemComponentLoader() {
        super("kinetic-weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableInt delayTick = ResolvableInt.autoOrNull(componentSection.getString("delay_ticks"));
        ResolvableInt contactCooldownTicks = ResolvableInt.autoOrNull(componentSection.getString("contact_cooldown_ticks"));
        ResolvableKineticCondition dismountConditions = this.loadKineticCondition(componentSection.getConfigurationSection("dismount_conditions"));
        ResolvableKineticCondition knockbackConditions = this.loadKineticCondition(componentSection.getConfigurationSection("knockback_conditions"));
        ResolvableKineticCondition damageConditions = this.loadKineticCondition(componentSection.getConfigurationSection("damage_conditions"));
        ResolvableFloat forwardMovement = ResolvableFloat.autoOrNull(componentSection.getString("forward_movement"));
        ResolvableFloat damageMultiplier = ResolvableFloat.autoOrNull(componentSection.getString("damage_multiplier"));
        ResolvableNamespacedKey sound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("sound"));
        ResolvableNamespacedKey hitSound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("hit_sound"));

        return new KineticWeaponComponent(
                contactCooldownTicks,
                delayTick,
                dismountConditions,
                knockbackConditions,
                damageConditions,
                forwardMovement,
                damageMultiplier,
                sound,
                hitSound
        );
    }

    private ResolvableKineticCondition loadKineticCondition(@Nullable ConfigurationSection section) {
        if (section == null) return null;
        ResolvableInt maxDurationTicks = ResolvableInt.autoOrNull(section.getString("max_duration_ticks"));
        ResolvableFloat minSpeed = ResolvableFloat.autoOrNull(section.getString("min_speed"));
        ResolvableFloat minRelativeSpeed = ResolvableFloat.autoOrNull(section.getString("min_relative_speed"));
        return new ResolvableKineticCondition(maxDurationTicks, minSpeed, minRelativeSpeed);
    }
}
