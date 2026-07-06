package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableKineticCondition;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class KineticWeaponComponent extends ItemComponent {
    private final ResolvableInt contactCooldownTicks;
    private final ResolvableInt delayTicks;
    private final ResolvableKineticCondition dismountConditions;
    private final ResolvableKineticCondition knockbackConditions;
    private final ResolvableKineticCondition damageConditions;
    private final ResolvableFloat forwardMovement;
    private final ResolvableFloat damageMultiplier;
    private final ResolvableNamespacedKey sound;
    private final ResolvableNamespacedKey hitSound;

    public KineticWeaponComponent(ResolvableInt contactCooldownTicks, ResolvableInt delayTicks, ResolvableKineticCondition dismountConditions, ResolvableKineticCondition knockbackConditions, ResolvableKineticCondition damageConditions, ResolvableFloat forwardMovement, ResolvableFloat damageMultiplier, ResolvableNamespacedKey sound, ResolvableNamespacedKey hitSound) {
        this.contactCooldownTicks = contactCooldownTicks;
        this.delayTicks = delayTicks;
        this.dismountConditions = dismountConditions;
        this.knockbackConditions = knockbackConditions;
        this.damageConditions = damageConditions;
        this.forwardMovement = forwardMovement;
        this.damageMultiplier = damageMultiplier;
        this.sound = sound;
        this.hitSound = hitSound;
    }


    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        KineticWeapon.Builder builder = KineticWeapon.kineticWeapon();

        Resolvable.applyResolvable(context, this.contactCooldownTicks, builder::contactCooldownTicks);
        Resolvable.applyResolvable(context, this.delayTicks, builder::delayTicks);
        Resolvable.applyResolvable(context, this.dismountConditions, builder::dismountConditions);
        Resolvable.applyResolvable(context, this.knockbackConditions, builder::knockbackConditions);
        Resolvable.applyResolvable(context, this.damageConditions, builder::damageConditions);
        Resolvable.applyResolvable(context, this.forwardMovement, builder::forwardMovement);
        Resolvable.applyResolvable(context, this.damageMultiplier, builder::damageMultiplier);
        Resolvable.applyResolvable(context, this.sound, builder::sound);
        Resolvable.applyResolvable(context, this.hitSound, builder::hitSound);

        itemStack.setData(DataComponentTypes.KINETIC_WEAPON, builder.build());

    }
}
