package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResolvableKineticCondition implements Resolvable<KineticWeapon.Condition> {
    private final @Nullable ResolvableInt maxDurationTicks;
    private final @Nullable ResolvableFloat minSpeed;
    private final @Nullable ResolvableFloat minRelativeSpeed;

    public ResolvableKineticCondition(
            @Nullable ResolvableInt maxDurationTicks,
            @Nullable ResolvableFloat minSpeed,
            @Nullable ResolvableFloat minRelativeSpeed
    ) {
        this.maxDurationTicks = maxDurationTicks;
        this.minSpeed = minSpeed;
        this.minRelativeSpeed = minRelativeSpeed;
    }

    @Override
    public KineticWeapon.@Nullable Condition resolve(@NotNull BuildContext context) {
        Integer resolvedMaxDurationTicks = Resolvable.resolve(context, this.maxDurationTicks);
        Float resolvedMinSpeed = Resolvable.resolve(context, this.minSpeed);
        Float resolvedMinRelativeSpeed = Resolvable.resolve(context, this.minRelativeSpeed);

        if (resolvedMaxDurationTicks == null || resolvedMinSpeed == null || resolvedMinRelativeSpeed == null) {
            return null;
        }

        return KineticWeapon.condition(resolvedMaxDurationTicks, resolvedMinSpeed, resolvedMinRelativeSpeed);
    }

    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.maxDurationTicks != null) map.put("max-duration-ticks", this.maxDurationTicks.serialize());
        if (this.minSpeed != null) map.put("min-speed", this.minSpeed.serialize());
        if (this.minRelativeSpeed != null) map.put("min-relative-speed", this.minRelativeSpeed.serialize());
        return map;
    }
}
