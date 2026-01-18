package fr.maxlego08.menu.loader.components;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.jetbrains.annotations.NotNull;

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
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(shape.toUpperCase());
                builder.with(type);
            } catch (IllegalArgumentException e) {
                return Optional.empty();
            }
        } else {
            return Optional.empty();
        }
        Object colorsObj = data.get("colors");
        if (colorsObj != null) {
            Color color = parseColor(colorsObj);
            if (color != null) {
                builder.withColor(color);
            }
        }
        Object fadeColorsObj = data.get("fade_colors");
        if (fadeColorsObj != null) {
            Color fadeColor = parseColor(fadeColorsObj);
            if (fadeColor != null) {
                builder.withFade(fadeColor);
            }
        }
        Object hasTrailObj = data.get("has_trail");
        if (hasTrailObj != null) {
            boolean hasTrail = (boolean) hasTrailObj;
            builder.trail(hasTrail);
        }

        Object hasTwinkleObj = data.get("has_twinkle");
        if (hasTwinkleObj != null) {
            boolean hasTwinkle = (boolean) hasTwinkleObj;
            builder.flicker(hasTwinkle);
        }
        return Optional.of(builder.build());
    }
}
