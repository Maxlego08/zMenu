package fr.maxlego08.menu.api.utils.itemstack;

import org.bukkit.damage.DamageType;

import java.util.List;

/**
 * @deprecated not used by zMenu.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
public record ZDamageReductionRecord(
    List<DamageType> damageTypes,
    float base,
    float factor,
    float horizontalBlockingAngle
) {
}
