package fr.maxlego08.menu.api.utils.resolvable.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.item.SwingAnimation;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

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

    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.animation != null) map.put("type", this.animation.serialize());
        if (this.duration != null) map.put("duration", this.duration.serialize());
        return map;
    }
}
