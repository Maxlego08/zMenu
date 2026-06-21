package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Consumer;

public abstract class ResolvableKineticCondition<T> {

    protected final @Nullable ResolvableInt maxDurationTicks;
    protected final @Nullable ResolvableFloat minSpeed;
    protected final @Nullable ResolvableFloat minRelativeSpeed;

    public ResolvableKineticCondition(
            @Nullable ResolvableInt maxDurationTicks,
            @Nullable ResolvableFloat minSpeed,
            @Nullable ResolvableFloat minRelativeSpeed
    ) {
        this.maxDurationTicks = maxDurationTicks;
        this.minSpeed = minSpeed;
        this.minRelativeSpeed = minRelativeSpeed;
    }

    @NotNull
    public abstract T applyTo(@NotNull T component,final @NotNull BuildContext context);

    protected <X> void applyResolvable(@NotNull Consumer<X> setter, @Nullable Resolvable<X> resolvable, @NotNull BuildContext context) {
        if (resolvable != null) {
            X resolvedValue = resolvable.resolve(context);
            if (resolvedValue != null) {
                setter.accept(resolvedValue);
            }
        }
    }
}
