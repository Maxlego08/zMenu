package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.damage.DamageType;

import java.util.List;

public record ZDamageReductionRecord(
    List<DamageType> damageTypes,
    float base,
    float factor,
    float horizontalBlockingAngle
) {
}
