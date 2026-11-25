package fr.maxlego08.menu.loader.components;

import fr.maxlego08.menu.api.annotations.AutoComponentLoader;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.components.FireworksComponent;
import fr.maxlego08.menu.api.itemstack.components.legacy.LegacyFireworksComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.SimpleResolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableColor;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Fireworks;
import org.bukkit.FireworkEffect;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.YamlDocument;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

@AutoComponentLoader
public class FireworksItemComponentLoader extends AbstractFireworkItemComponentLoader {

    public FireworksItemComponentLoader(){
        super("fireworks");
    }

    @Override
    public @Nullable ItemComponent load(@NotNull MenuItemStackContext context, @NotNull File file, @NotNull YamlDocument configuration, @NotNull String path, @Nullable Section componentSection) {
        if (componentSection == null) return null;

        ResolvableInt flightDuration = this.asResolvableInt(componentSection, "flight-duration", 1);

        List<ResolvableFireworkEffect> effects = new ArrayList<>();
        List<Map<?, ?>> mapList = componentSection.getMapList("explosions");
        for (var rawMap : mapList) {
            @SuppressWarnings("unchecked")
            Map<String, Object> effectMap = (Map<String, Object>) rawMap;
            ResolvableFireworkEffect effect = this.parseFireworkEffect(effectMap);
            if (effect != null) effects.add(effect);
        }

        if (effects.isEmpty() && !flightDuration.isDynamic() && flightDuration.getResolvedValue() != null && flightDuration.getResolvedValue() == 1) {
            return null;
        }
        return MinecraftVersion.isServerAtLeast("1.21.3")
                ? new FireworksComponent(flightDuration, effects)
                : new LegacyFireworksComponent(flightDuration, effects);
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    public ItemComponent load(MenuItemStackContext context, File file, YamlConfiguration configuration, String path, ConfigurationSection componentSection) {
        YamlDocument configurationDocument = configuration == null ? null : YamlSections.toDocument(configuration);
        return this.load(context, file, configurationDocument, path, YamlSections.toSection(configurationDocument, componentSection));
    }

    @Override
    public @Nullable ItemComponent fromItemStack(@NotNull ItemStack itemStack) {
        Fireworks fireworks = itemStack.getData(DataComponentTypes.FIREWORKS);
        if (fireworks == null) return null;
        if (fireworks.effects().isEmpty() && fireworks.flightDuration() == 1) return null;

        List<ResolvableFireworkEffect> effects = new ArrayList<>(fireworks.effects().size());
        for (FireworkEffect effect : fireworks.effects()) {
            ResolvableFireworkEffect resolvableEffect = toResolvableFireworkEffect(effect);
            if (resolvableEffect == null) return null;
            effects.add(resolvableEffect);
        }
        return new FireworksComponent(ResolvableInt.of(fireworks.flightDuration()), effects);
    }

    @Override
    public @Nullable ItemComponent fromItemMeta(@NotNull ItemStack itemStack) {
        if (!(itemStack.getItemMeta() instanceof FireworkMeta fireworkMeta)) return null;
        int power = fireworkMeta.getPower();
        if (!fireworkMeta.hasEffects() && (power == 0 || power == 1)) return null;

        List<ResolvableFireworkEffect> effects = new ArrayList<>(fireworkMeta.getEffectsSize());
        for (FireworkEffect effect : fireworkMeta.getEffects()) {
            ResolvableFireworkEffect resolvableEffect = toResolvableFireworkEffect(effect);
            if (resolvableEffect == null) return null;
            effects.add(resolvableEffect);
        }
        return new LegacyFireworksComponent(ResolvableInt.of(power), effects);
    }

    private @Nullable ResolvableFireworkEffect parseFireworkEffect(Map<String, Object> data) {
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
            try {
                FireworkEffect.Type type = FireworkEffect.Type.valueOf(((String) shapeObj).toUpperCase(Locale.ROOT));
                shape = SimpleResolvable.of(type, s -> {
                    try {
                        return FireworkEffect.Type.valueOf(s.toUpperCase(Locale.ROOT));
                    } catch (IllegalArgumentException e) {
                        return null;
                    }
                });
            } catch (IllegalArgumentException e) {
                return null;
            }
        }

        ResolvableColor color = null;
        Object colorsObj = data.get("colors");
        if (colorsObj != null) color = ResolvableColor.of(colorsObj);

        ResolvableColor fadeColor = null;
        Object fadeColorsObj = data.get("fade-colors");
        if (fadeColorsObj != null) fadeColor = ResolvableColor.of(fadeColorsObj);

        ResolvableBoolean hasTrail = null;
        Object hasTrailObj = data.get("has-trail");
        if (hasTrailObj instanceof Boolean bool) hasTrail = ResolvableBoolean.of(bool);
        else if (hasTrailObj instanceof String expr) hasTrail = ResolvableBoolean.of(expr);

        ResolvableBoolean hasTwinkle = null;
        Object hasTwinkleObj = data.get("has-twinkle");
        if (hasTwinkleObj instanceof Boolean bool) hasTwinkle = ResolvableBoolean.of(bool);
        else if (hasTwinkleObj instanceof String expr) hasTwinkle = ResolvableBoolean.of(expr);

        return new ResolvableFireworkEffect(shape, color, fadeColor, hasTrail, hasTwinkle);
    }
}
