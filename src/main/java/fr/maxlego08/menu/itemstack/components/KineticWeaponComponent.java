package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record KineticWeaponComponent(
    int delayTicks,
    @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> damageCondition,
    @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> dismountCondition,
    @NotNull Optional<org.bukkit.inventory.meta.components.KineticWeaponComponent.Condition> knockbackCondition,
    float forwardMovement,
    float damageMultiplier,
    @NotNull Optional<Sound> sound,
    @NotNull Optional<Sound> hitSound
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
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
