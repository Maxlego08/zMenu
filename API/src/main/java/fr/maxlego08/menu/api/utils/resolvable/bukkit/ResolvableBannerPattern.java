package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.DyeColor;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ResolvableBannerPattern implements Resolvable<Pattern> {

    private final ResolvableEnum<DyeColor> color;
    private final ResolvableRegistryEntry<PatternType> patternType;

    public ResolvableBannerPattern(
            @NotNull ResolvableEnum<DyeColor> color,
            @NotNull ResolvableRegistryEntry<PatternType> patternType
    ) {
        this.color = color;
        this.patternType = patternType;
    }

    @Nullable
    public static ResolvableBannerPattern fromMap(@NotNull Map<String, Object> map) {
        Object colorObj = map.get("color");
        Object patternObj = map.get("pattern");
        if (!(colorObj instanceof String colorStr) || !(patternObj instanceof String patternStr)) return null;

        ResolvableEnum<DyeColor> color = ResolvableEnum.auto(DyeColor.class, colorStr);
        ResolvableRegistryEntry<PatternType> patternTypeRegistry = ResolvableRegistry.auto(patternStr, RegistryKey.BANNER_PATTERN);

        return new ResolvableBannerPattern(color, patternTypeRegistry);
    }

    @Override
    public @Nullable Pattern resolve(@NotNull BuildContext context) {
        DyeColor color = this.color.resolve(context);
        PatternType patternType = this.patternType.resolve(context);
        if (color == null || patternType == null) return null;

        return new Pattern(color, patternType);
    }

    /**
     * Writes the pattern map {@link #fromMap} reads: {@code color} as the dye name and {@code pattern} as a registry key.
     */
    @Override
    public @NotNull Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("color", this.color.serialize());
        map.put("pattern", this.patternType.serialize());
        return map;
    }
}
