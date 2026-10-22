package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.utils.ColorUtils;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractFireworkItemComponentLoader extends AbstractColorItemComponentLoader {
    public AbstractFireworkItemComponentLoader(@NotNull String componentName) {
        super(componentName);
    }

    protected Optional<FireworkEffect> loadFireworkEffect(@NotNull Map<String, Object> data){
        FireworkEffect.Builder builder = FireworkEffect.builder();
        String shape = (String) data.get("shape");
        if (shape != null) {
            try {
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(shape.toUpperCase(Locale.ROOT));
                builder.with(type);
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
        Object colorsObj = data.get("colors");
        if (colorsObj != null) {
            Color color = ColorUtils.parse(colorsObj);
            if (color != null) {
                builder.withColor(color);
            }
        }
        Object fadeColorsObj = data.get("fade-colors");
        if (fadeColorsObj != null) {
            Color fadeColor = ColorUtils.parse(fadeColorsObj);
            if (fadeColor != null) {
                builder.withFade(fadeColor);
            }
        }
        Object hasTrailObj = data.get("has-trail");
        if (hasTrailObj != null) {
            boolean hasTrail = (boolean) hasTrailObj;
            builder.trail(hasTrail);
        }

        Object hasTwinkleObj = data.get("has-twinkle");
        if (hasTwinkleObj != null) {
            boolean hasTwinkle = (boolean) hasTwinkleObj;
            builder.flicker(hasTwinkle);
        }
        return Optional.of(builder.build());
    }

    /**
     * @return The effect as the firework loaders read it, or null when it has no color or more than one color or fade color,
     * since the configuration holds a single color and a single fade color.
     */
    protected static @Nullable ResolvableFireworkEffect toResolvableFireworkEffect(@NotNull FireworkEffect effect) {
        if (effect.getColors().size() != 1 || effect.getFadeColors().size() > 1) return null;
        Color fadeColor = effect.getFadeColors().isEmpty() ? null : effect.getFadeColors().getFirst();
        return new ResolvableFireworkEffect(
                SimpleResolvable.of(effect.getType(), s -> {
                    try {
                        return FireworkEffect.Type.valueOf(s.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                }),
                ResolvableColor.of(effect.getColors().getFirst()),
                fadeColor == null ? null : ResolvableColor.of(fadeColor),
                ResolvableBoolean.of(effect.hasTrail()),
                ResolvableBoolean.of(effect.hasFlicker())
        );
    }
}
