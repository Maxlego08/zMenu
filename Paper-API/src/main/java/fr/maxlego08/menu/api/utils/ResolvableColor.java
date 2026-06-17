package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.placeholder.Placeholder;
import org.bukkit.Color;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public final class ResolvableColor {
    private final @Nullable Color staticColor;
    private final @Nullable String dynamicValue;

    private ResolvableColor(@Nullable Color staticColor, @Nullable String dynamicValue) {
        this.staticColor = staticColor;
        this.dynamicValue = dynamicValue;
    }

    @Nullable
    public static ResolvableColor of(@NotNull Object raw) {
        Color parsed = ColorUtils.parse(raw);
        if (parsed != null) {
            return new ResolvableColor(parsed, null);
        }

        if (raw instanceof String str && !str.isBlank()) {
            return new ResolvableColor(null, str);
        }

        return null;
    }

    @NotNull
    public static ResolvableColor of(@NotNull Color color) {
        return new ResolvableColor(color, null);
    }

    @NotNull
    public static ResolvableColor of(@NotNull String dynamicValue) {
        return new ResolvableColor(null, dynamicValue);
    }

    public boolean isDynamic() {
        return this.dynamicValue != null;
    }

    @Nullable
    public String getDynamicValue() {
        return this.dynamicValue;
    }

    @Nullable
    public Color resolve(@NotNull BuildContext context) {
        if (this.staticColor != null) {
            return this.staticColor;
        }

        Player player = context.getPlayer();

        String parsed = Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, context.getPlaceholders().parse(this.dynamicValue));


        return ColorUtils.parse(parsed);
    }
}
