package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ResolvableFireworkEffect implements Resolvable<FireworkEffect> {

    private final @Nullable Resolvable<FireworkEffect.Type> shape;
    private final @Nullable ResolvableColor color;
    private final @Nullable ResolvableColor fadeColor;
    private final @Nullable ResolvableBoolean hasTrail;
    private final @Nullable ResolvableBoolean hasTwinkle;

    public ResolvableFireworkEffect(
            @Nullable Resolvable<FireworkEffect.Type> shape,
            @Nullable ResolvableColor color,
            @Nullable ResolvableColor fadeColor,
            @Nullable ResolvableBoolean hasTrail,
            @Nullable ResolvableBoolean hasTwinkle
    ) {
        this.shape = shape;
        this.color = color;
        this.fadeColor = fadeColor;
        this.hasTrail = hasTrail;
        this.hasTwinkle = hasTwinkle;
    }

    @Override
    public @Nullable FireworkEffect resolve(@NotNull BuildContext context) {
        FireworkEffect.Builder builder = FireworkEffect.builder();

        if (this.shape != null) {
            FireworkEffect.Type resolved = this.shape.resolve(context);
            if (resolved == null) return null;
            builder.with(resolved);
        } else {
            return null;
        }

        Resolvable.applyResolvable(context, this.color, builder::withColor);

        Resolvable.applyResolvable(context, this.fadeColor, builder::withFade);

        Resolvable.applyResolvable(context, this.hasTrail, builder::trail);

        Resolvable.applyResolvable(context, this.hasTwinkle, builder::flicker);

        return builder.build();
    }
}
