package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

@SuppressWarnings("unused")
public class KineticWeaponComponent extends ItemComponent {
    private final int delayTicks;
    private final @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> damageCondition;
    private final @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> dismountCondition;
    private final @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> knockbackCondition;
    private final float forwardMovement;
    private final float damageMultiplier;
    private final @NotNull Optional<Sound> sound;
    private final @NotNull Optional<Sound> hitSound;

    public KineticWeaponComponent(int delayTicks, @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> damageCondition,
            @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> dismountCondition, @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> knockbackCondition,
            float forwardMovement, float damageMultiplier, @NotNull Optional<Sound> sound, @NotNull Optional<Sound> hitSound) {
        this.delayTicks = delayTicks;
        this.damageCondition = damageCondition;
        this.dismountCondition = dismountCondition;
        this.knockbackCondition = knockbackCondition;
        this.forwardMovement = forwardMovement;
        this.damageMultiplier = damageMultiplier;
        this.sound = sound;
        this.hitSound = hitSound;
    }

    public int getDelayTicks() {
        return delayTicks;
    }

    public @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> getDamageCondition() {
        return damageCondition;
    }

    public @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> getDismountCondition() {
        return dismountCondition;
    }

    public @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> getKnockbackCondition() {
        return knockbackCondition;
    }

    public float getForwardMovement() {
        return forwardMovement;
    }

    public float getDamageMultiplier() {
        return damageMultiplier;
    }

    public @NotNull Optional<Sound> getSound() {
        return sound;
    }

    public @NotNull Optional<Sound> getHitSound() {
        return hitSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.KineticWeaponComponent kineticWeapon = itemMeta.getKineticWeapon();

            kineticWeapon.setDelayTicks(this.delayTicks);

            this.damageCondition.ifPresent(kineticWeapon::setDamageConditions);
            this.dismountCondition.ifPresent(kineticWeapon::setDismountConditions);
            this.knockbackCondition.ifPresent(kineticWeapon::setKnockbackConditions);

            kineticWeapon.setForwardMovement(this.forwardMovement);
            kineticWeapon.setDamageMultipler(this.damageMultiplier);

            this.sound.ifPresent(kineticWeapon::setSound);
            this.hitSound.ifPresent(kineticWeapon::setHitSound);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
