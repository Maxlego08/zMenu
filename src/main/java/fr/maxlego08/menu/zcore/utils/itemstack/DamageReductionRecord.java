package fr.maxlego08.menu.zcore.utils.itemstack;

import org.bukkit.damage.DamageType;

import java.util.List;

public record DamageReductionRecord(
    List<DamageType> damageTypes,
    float base,
    float factor,
    float horizontalBlockingAngle
) {
}
