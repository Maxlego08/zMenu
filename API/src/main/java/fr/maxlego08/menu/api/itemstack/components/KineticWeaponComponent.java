package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.SpigotKineticWeaponResolvable;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableSound;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class KineticWeaponComponent extends ItemComponent {
    private final @Nullable ResolvableInt delayTicks;
    private final @Nullable SpigotKineticWeaponResolvable damageCondition;
    private final @Nullable SpigotKineticWeaponResolvable dismountCondition;
    private final @Nullable SpigotKineticWeaponResolvable knockbackCondition;
    private final @Nullable ResolvableFloat forwardMovement;
    private final @Nullable ResolvableFloat damageMultiplier;
    private final @Nullable ResolvableSound sound;
    private final @Nullable ResolvableSound hitSound;

    public KineticWeaponComponent(
            @Nullable ResolvableInt delayTicks,
            @Nullable SpigotKineticWeaponResolvable damageCondition,
            @Nullable SpigotKineticWeaponResolvable dismountCondition,
            @Nullable SpigotKineticWeaponResolvable knockbackCondition,
            @Nullable ResolvableFloat forwardMovement,
            @Nullable ResolvableFloat damageMultiplier,
            @Nullable ResolvableSound sound,
            @Nullable ResolvableSound hitSound
    ) {
        this.delayTicks = delayTicks;
        this.damageCondition = damageCondition;
        this.dismountCondition = dismountCondition;
        this.knockbackCondition = knockbackCondition;
        this.forwardMovement = forwardMovement;
        this.damageMultiplier = damageMultiplier;
        this.sound = sound;
        this.hitSound = hitSound;
    }

    public @Nullable ResolvableInt getDelayTicks() {
        return this.delayTicks;
    }

    public @Nullable SpigotKineticWeaponResolvable getDamageCondition() {
        return this.damageCondition;
    }

    public @Nullable SpigotKineticWeaponResolvable getDismountCondition() {
        return this.dismountCondition;
    }

    public @Nullable SpigotKineticWeaponResolvable getKnockbackCondition() {
        return this.knockbackCondition;
    }

    public @Nullable ResolvableFloat getForwardMovement() {
        return this.forwardMovement;
    }

    public @Nullable ResolvableFloat getDamageMultiplier() {
        return this.damageMultiplier;
    }

    public @Nullable ResolvableSound getSound() {
        return this.sound;
    }

    public @Nullable ResolvableSound getHitSound() {
        return this.hitSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.KineticWeaponComponent kineticWeapon = itemMeta.getKineticWeapon();

        this.applyResolvable(context, kineticWeapon::setDelayTicks, this.delayTicks);

        if (this.damageCondition != null) {
            kineticWeapon.setDamageConditions(this.damageCondition.applyTo(kineticWeapon.getDamageConditions(), context));
        }
        if (this.dismountCondition != null) {
            kineticWeapon.setDismountConditions(this.dismountCondition.applyTo(kineticWeapon.getDismountConditions(), context));
        }
        if (this.knockbackCondition != null) {
            kineticWeapon.setKnockbackConditions(this.knockbackCondition.applyTo(kineticWeapon.getKnockbackConditions(), context));
        }

        this.applyResolvable(context, kineticWeapon::setForwardMovement, this.forwardMovement);
        this.applyResolvable(context, kineticWeapon::setDamageMultipler, this.damageMultiplier);
        this.applyResolvable(context, kineticWeapon::setSound, this.sound);
        this.applyResolvable(context, kineticWeapon::setHitSound, this.hitSound);

        itemStack.setItemMeta(itemMeta);
    }
}
