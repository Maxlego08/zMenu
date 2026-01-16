package fr.maxlego08.common.loader.components;

import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.NamespacedKey;
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
                NamespacedKey key = NamespacedKey.fromString(shape.toLowerCase());
                if (key != null) {
                    FireworkEffect.Type type = FireworkEffect.Type.valueOf(key.getKey().toUpperCase());
                    builder.with(type);
                }
            } catch (IllegalArgumentException ignored) {
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
        boolean hasTrail = (boolean) data.get("has_trail");
        builder.trail(hasTrail);
        boolean hasTwinkle = (boolean) data.get("has_twinkle");
        builder.flicker(hasTwinkle);
        return Optional.of(builder.build());
    }
}
