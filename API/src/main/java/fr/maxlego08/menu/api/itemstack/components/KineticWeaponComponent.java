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

import java.util.LinkedHashMap;
import java.util.Map;

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

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.delayTicks != null) map.put("delay-ticks", this.delayTicks.serialize());
        if (this.contactCooldownTicks != null) map.put("contact-cooldown-ticks", this.contactCooldownTicks.serialize());
        if (this.dismountConditions != null) map.put("dismount-conditions", this.dismountConditions.serialize());
        if (this.knockbackConditions != null) map.put("knockback-conditions", this.knockbackConditions.serialize());
        if (this.damageConditions != null) map.put("damage-conditions", this.damageConditions.serialize());
        if (this.forwardMovement != null) map.put("forward-movement", this.forwardMovement.serialize());
        if (this.damageMultiplier != null) map.put("damage-multiplier", this.damageMultiplier.serialize());
        if (this.sound != null) map.put("sound", this.sound.serialize());
        if (this.hitSound != null) map.put("hit-sound", this.hitSound.serialize());
        return map;
    }
}
