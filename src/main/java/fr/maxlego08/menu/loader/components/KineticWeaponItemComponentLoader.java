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
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import net.kyori.adventure.key.Key;
import org.bukkit.NamespacedKey;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
@SinceVersion("1.21.11")
public final class KineticWeaponItemComponentLoader extends ItemComponentLoader {

    public KineticWeaponItemComponentLoader() {
        super("kinetic-weapon");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableInt delayTick = ResolvableInt.autoOrNull(componentSection.getString("delay-ticks"));
        ResolvableInt contactCooldownTicks = ResolvableInt.autoOrNull(componentSection.getString("contact-cooldown-ticks"));
        ResolvableKineticCondition dismountConditions = this.loadKineticCondition(componentSection.getSection("dismount-conditions"));
        ResolvableKineticCondition knockbackConditions = this.loadKineticCondition(componentSection.getSection("knockback-conditions"));
        ResolvableKineticCondition damageConditions = this.loadKineticCondition(componentSection.getSection("damage-conditions"));
        ResolvableFloat forwardMovement = ResolvableFloat.autoOrNull(componentSection.getString("forward-movement"));
        ResolvableFloat damageMultiplier = ResolvableFloat.autoOrNull(componentSection.getString("damage-multiplier"));
        ResolvableNamespacedKey sound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("sound"));
        ResolvableNamespacedKey hitSound = ResolvableNamespacedKey.autoOrNull(componentSection.getString("hit-sound"));

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

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        KineticWeapon kineticWeapon = itemStack.getData(DataComponentTypes.KINETIC_WEAPON);
        if (kineticWeapon == null) return null;
        return new KineticWeaponComponent(
                ResolvableInt.of(kineticWeapon.contactCooldownTicks()),
                ResolvableInt.of(kineticWeapon.delayTicks()),
                this.toResolvableCondition(kineticWeapon.dismountConditions()),
                this.toResolvableCondition(kineticWeapon.knockbackConditions()),
                this.toResolvableCondition(kineticWeapon.damageConditions()),
                ResolvableFloat.of(kineticWeapon.forwardMovement()),
                ResolvableFloat.of(kineticWeapon.damageMultiplier()),
                this.toResolvableKey(kineticWeapon.sound()),
                this.toResolvableKey(kineticWeapon.hitSound())
        );
    }

    private @Nullable ResolvableKineticCondition toResolvableCondition(KineticWeapon.@Nullable Condition condition) {
        if (condition == null) return null;
        return new ResolvableKineticCondition(ResolvableInt.of(condition.maxDurationTicks()), ResolvableFloat.of(condition.minSpeed()), ResolvableFloat.of(condition.minRelativeSpeed()));
    }

    private @Nullable ResolvableNamespacedKey toResolvableKey(@Nullable Key key) {
        if (key == null) return null;
        NamespacedKey namespacedKey = NamespacedKey.fromString(key.asString());
        return namespacedKey == null ? null : ResolvableNamespacedKey.of(namespacedKey);
    }

    private ResolvableKineticCondition loadKineticCondition(@Nullable Section section) {
        if (section == null) return null;
        ResolvableInt maxDurationTicks = ResolvableInt.autoOrNull(section.getString("max-duration-ticks"));
        ResolvableFloat minSpeed = ResolvableFloat.autoOrNull(section.getString("min-speed"));
        ResolvableFloat minRelativeSpeed = ResolvableFloat.autoOrNull(section.getString("min-relative-speed"));
        return new ResolvableKineticCondition(maxDurationTicks, minSpeed, minRelativeSpeed);
    }
}
