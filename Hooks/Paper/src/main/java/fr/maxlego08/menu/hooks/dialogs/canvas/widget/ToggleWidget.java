package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;

/**
 * A switch between {@code true} and {@code false}.
 *
 * <pre>
 * music:
 *   type: toggle
 *   default: "true"
 *   x: 10
 *   y: 20
 *   glyph-on: widgets/toggle_on
 *   glyph-off: widgets/toggle_off
 *   label: "&lt;white&gt;Music"     # optional, right of the glyph
 *   label-on: "&lt;green&gt;Music on"  # optional, the label when on
 *   label-off: "&lt;red&gt;Music off"  # optional, the label when off
 *   width: 80                   # optional, the clickable width (the glyph's by default)
 * </pre>
 * {@code label}, {@code label-on} and {@code label-off} are parts (see {@link WidgetPart}): each can be moved
 * relative to the widget, and its {@code hover} is the tooltip of the switch in that state. Plus the keys of
 * {@link WidgetSettings}.
 */
public final class ToggleWidget extends WidgetElement {

    public static final String TYPE = "toggle";
    private static final int LABEL_GAP = 4;
    private static final int TEXT_HEIGHT = 8;

    private final String glyphOn;
    private final String glyphOff;
    private final WidgetPart label;
    private final WidgetPart labelOn;
    private final WidgetPart labelOff;
    private final String font;
    private final Integer width;

    public ToggleWidget(@NotNull WidgetSettings settings, @NotNull String glyphOn, @NotNull String glyphOff, @Nullable String label, @NotNull String font,
                        @Nullable Integer width) {
        this(settings, glyphOn, glyphOff, WidgetPart.ofText(label), WidgetPart.NONE, WidgetPart.NONE, font, width);
    }

    public ToggleWidget(@NotNull WidgetSettings settings, @NotNull String glyphOn, @NotNull String glyphOff, @NotNull WidgetPart label,
                        @NotNull WidgetPart labelOn, @NotNull WidgetPart labelOff, @NotNull String font, @Nullable Integer width) {
        super(settings);
        this.glyphOn = glyphOn;
        this.glyphOff = glyphOff;
        this.label = label;
        this.labelOn = labelOn;
        this.labelOff = labelOff;
        this.font = font;
        this.width = width;
    }

    /**
     * @param value A value.
     * @return True for {@code true}, {@code yes} and {@code 1}.
     */
    public static boolean isOn(@NotNull String value) {
        return switch (value.trim().toLowerCase(Locale.ROOT)) {
            case "true", "yes", "1" -> true;
            default -> false;
        };
    }

    private int[] size(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        int glyphWidth = 0;
        int height = 0;
        for (String reference : new String[]{this.glyphOn, this.glyphOff}) {
            Optional<ResolvedGlyph> glyph = glyphs.apply(reference);
            if (glyph.isEmpty()) continue;
            glyphWidth = Math.max(glyphWidth, glyph.get().visualWidth());
            height = Math.max(height, glyph.get().height());
        }
        return new int[]{glyphWidth, Math.max(height, 1)};
    }

    private @NotNull WidgetPart labelFor(boolean on) {
        return (on ? this.labelOn : this.labelOff).orElse(this.label);
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        this.glyph(variants, this.glyphOn, this.settings.y());
        this.glyph(variants, this.glyphOff, this.settings.y());
        int[] size = this.size(reference -> Optional.ofNullable(variants.resolve(reference)));
        int labelY = Math.max(0, (size[1] - TEXT_HEIGHT) / 2);
        collectTexts(variants, List.of(this.labelFor(true), this.labelFor(false)), null, this.settings.y(), labelY, this.font);
        variants.area(this.settings.y(), this.width == null ? size[0] : this.width, size[1]);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        boolean on = isOn(value);
        int x = this.settings.x();
        int y = this.settings.y();
        int[] size = this.size(context.canvasManager()::resolveGlyph);
        WidgetPart part = this.labelFor(on);

        this.glyph(context, builder, on ? this.glyphOn : this.glyphOff, x, y);
        this.drawText(context, builder, part, null, value, x, y, size[0] + LABEL_GAP, Math.max(0, (size[1] - TEXT_HEIGHT) / 2), 0, HAlign.LEFT, this.font);
        builder.area(x, y, this.width == null ? size[0] : this.width, size[1], this.change(context, on ? "false" : "true"), this.hover(context, value, part));
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        if (this.width == null) return List.of();
        return outside(this.settings.x() + this.width, this.settings.y(), canvasWidth, canvasHeight, "the toggle's click area");
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "false");
        section.set("glyph-on", this.glyphOn);
        section.set("glyph-off", this.glyphOff);
        this.label.serialize(section, "label", false);
        this.labelOn.serialize(section, "label-on", false);
        this.labelOff.serialize(section, "label-off", false);
        if (!this.font.equals("default")) section.set("font", this.font);
        if (this.width != null) section.set("width", this.width);
    }
}
