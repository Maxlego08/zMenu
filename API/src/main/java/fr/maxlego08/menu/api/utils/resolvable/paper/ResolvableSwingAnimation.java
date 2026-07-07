package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

public final class ResolvableSwingAnimation implements Resolvable<SwingAnimation> {
    private final ResolvableEnum<SwingAnimation.Animation> animation;
    private final ResolvableInt duration;

    public ResolvableSwingAnimation(ResolvableEnum<SwingAnimation.Animation> animation, ResolvableInt duration) {
        this.animation = animation;
        this.duration = duration;
    }

    @Override
    public @Nullable SwingAnimation resolve(@NotNull BuildContext context) {
        SwingAnimation.Builder builder = SwingAnimation.swingAnimation();

        Resolvable.applyResolvable(context, this.animation, builder::type);
        Resolvable.applyResolvable(context, this.duration, builder::duration);

        return builder.build();
    }
}
