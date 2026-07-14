package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.item.KineticWeapon;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
}
