package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.SpigotKineticWeaponResolvable;
import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

@AutoComponentLoader
@SinceVersion("1.21.11")
public class SpigotKineticWeaponItemComponentLoader extends ItemComponentLoader {

    public SpigotKineticWeaponItemComponentLoader(){
        super("kinetic-weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;

        ResolvableInt delayTicks = this.asResolvableInt(componentSection, "delay-ticks", 0);
        SpigotKineticWeaponResolvable damageCondition = this.parseCondition(componentSection.getConfigurationSection("damage-conditions"));
        SpigotKineticWeaponResolvable dismountCondition = this.parseCondition(componentSection.getConfigurationSection("dismount-conditions"));
        SpigotKineticWeaponResolvable knockbackCondition = this.parseCondition(componentSection.getConfigurationSection("knockback-conditions"));
        ResolvableFloat forwardMovement = this.asResolvableFloat(componentSection, "forward-movement", 0f);
        ResolvableFloat damageMultiplier = this.asResolvableFloat(componentSection, "damage-multiplier", 1f);
        ResolvableSound sound = this.asResolvableSound(componentSection, "sound");
        ResolvableSound hitSound = this.asResolvableSound(componentSection, "hit-sound");

        return new fr.maxlego08.menu.api.itemstack.components.KineticWeaponComponent(
                delayTicks,
                damageCondition,
                dismountCondition,
                knockbackCondition,
                forwardMovement,
                damageMultiplier,
                sound,
                hitSound
        );
    }

    private @Nullable SpigotKineticWeaponResolvable parseCondition(@Nullable ConfigurationSection section) {
        if (section == null) return null;

        ResolvableInt maxDurationTicks = this.asResolvableInt(section, "max-duration-ticks", -1);
        if (!maxDurationTicks.isDynamic() && (maxDurationTicks.getResolvedValue() == null || maxDurationTicks.getResolvedValue() < 0)) {
            return null;
        }

        ResolvableFloat minSpeed = this.asResolvableFloat(section, "min-speed", 0f);
        ResolvableFloat minRelativeSpeed = this.asResolvableFloat(section, "min-relative-speed", 0f);

        return new SpigotKineticWeaponResolvable(maxDurationTicks, minSpeed, minRelativeSpeed);
    }
}
