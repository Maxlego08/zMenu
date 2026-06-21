package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageType;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.damage.DamageType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class ResolvableDamageReduction {

    private final List<ResolvableDamageType> damageTypes;
    private final ResolvableFloat base;
    private final ResolvableFloat factor;
    private final ResolvableFloat horizontalBlockingAngle;

    public ResolvableDamageReduction(
            @NotNull List<ResolvableDamageType> damageTypes,
            @NotNull ResolvableFloat base,
            @NotNull ResolvableFloat factor,
            @NotNull ResolvableFloat horizontalBlockingAngle
    ) {
        this.damageTypes = damageTypes;
        this.base = base;
        this.factor = factor;
        this.horizontalBlockingAngle = horizontalBlockingAngle;
    }

    @Nullable
    public static ResolvableDamageReduction fromMap(@NotNull Map<String, Object> map) {
        Object typeObj = map.get("type");
        if (typeObj == null) return null;

        List<ResolvableDamageType> damageTypes = new ArrayList<>();
        if (typeObj instanceof String typeStr) {
            damageTypes.add(ResolvableDamageType.autoOrNull(typeStr));
        } else if (typeObj instanceof List<?> typeList) {
            for (Object obj : typeList) {
                if (obj instanceof String typeStr) {
                    damageTypes.add(ResolvableDamageType.autoOrNull(typeStr));
                }
            }
        }
        if (damageTypes.isEmpty()) return null;

        ResolvableFloat base = resolvableFloatField(map, "base", 0f);
        ResolvableFloat factor = resolvableFloatField(map, "factor", 0f);
        ResolvableFloat horizontalBlockingAngle = resolvableFloatField(map, "horizontal-blocking-angle", 90f);

        return new ResolvableDamageReduction(damageTypes, base, factor, horizontalBlockingAngle);
    }

    private static ResolvableFloat resolvableFloatField(Map<String, Object> map, String key, float defaultValue) {
        Object value = map.get(key);
        if (value instanceof Number number) {
            return ResolvableFloat.of(number.floatValue());
        } else if (value instanceof String str) {
            return ResolvableFloat.of(str);
        }
        return ResolvableFloat.of(defaultValue);
    }

    public void applyTo(@NotNull org.bukkit.inventory.meta.components.BlocksAttacksComponent component, @NotNull BuildContext context) {
        List<DamageType> resolvedTypes = new ArrayList<>();
        for (ResolvableDamageType dt : this.damageTypes) {
            DamageType resolved = dt.resolve(context);
            if (resolved != null) {
                resolvedTypes.add(resolved);
            }
        }
        if (resolvedTypes.isEmpty()) return;

        Float baseValue = this.base.resolve(context);
        Float factorValue = this.factor.resolve(context);
        Float angleValue = this.horizontalBlockingAngle.resolve(context);
        if (baseValue == null || factorValue == null || angleValue == null) return;

        component.addDamageReduction(resolvedTypes, baseValue, factorValue, angleValue);
    }
}
