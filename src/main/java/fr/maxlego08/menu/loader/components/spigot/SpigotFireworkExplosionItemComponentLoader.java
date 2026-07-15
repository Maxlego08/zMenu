package fr.maxlego08.menu.loader.components.spigot;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.annotations.SinceVersion;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.FireworkExplosionComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.loader.components.AbstractFireworkItemComponentLoader;
import org.bukkit.FireworkEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Locale;
import java.util.Map;

@AutoComponentLoader
@SinceVersion("1.20.5")
public class SpigotFireworkExplosionItemComponentLoader extends AbstractFireworkItemComponentLoader {

    public SpigotFireworkExplosionItemComponentLoader(){
        super("firework-explosion");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlConfiguration configuration, @NotNull String path, @Nullable ConfigurationSection componentSection) {
        if (componentSection == null) return null;
        Map<String, Object> data = componentSection.getValues(true);

        Object shapeObj = data.get("shape");
        if (shapeObj == null) return null;

        Resolvable<FireworkEffect.Type> shape;
        if (shapeObj instanceof String shapeString && shapeString.contains("%")) {
            shape = SimpleResolvable.ofExpression(shapeString, s -> {
                try {
                    return FireworkEffect.Type.valueOf(s.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            });
        } else {
            FireworkEffect.Type type = FireworkEffect.Type.valueOf(((String) shapeObj).toUpperCase(Locale.ROOT));
            shape = SimpleResolvable.of(type, s -> {
                try {
                    return FireworkEffect.Type.valueOf(s.toUpperCase(Locale.ROOT));
                } catch (IllegalArgumentException e) {
                    return null;
                }
            });
        }

        ResolvableColor color = null;
        Object colorsObj = data.get("colors");
        if (colorsObj != null) color = ResolvableColor.of(colorsObj);

        ResolvableColor fadeColor = null;
        Object fadeColorsObj = data.get("fade_colors");
        if (fadeColorsObj != null) fadeColor = ResolvableColor.of(fadeColorsObj);

        ResolvableBoolean hasTrail = null;
        Object hasTrailObj = data.get("has_trail");
        if (hasTrailObj instanceof Boolean bool) hasTrail = ResolvableBoolean.of(bool);
        else if (hasTrailObj instanceof String expr) hasTrail = ResolvableBoolean.of(expr);

        ResolvableBoolean hasTwinkle = null;
        Object hasTwinkleObj = data.get("has_twinkle");
        if (hasTwinkleObj instanceof Boolean bool) hasTwinkle = ResolvableBoolean.of(bool);
        else if (hasTwinkleObj instanceof String expr) hasTwinkle = ResolvableBoolean.of(expr);

        return new FireworkExplosionComponent(new ResolvableFireworkEffect(shape, color, fadeColor, hasTrail, hasTwinkle));
    }
}
