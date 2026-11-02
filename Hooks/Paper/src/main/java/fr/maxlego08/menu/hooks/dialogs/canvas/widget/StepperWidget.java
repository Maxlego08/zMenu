package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A number with a minus and a plus button.
 *
 * <pre>
 * amount:
 *   type: stepper
 *   min: 1
 *   max: 64
 *   step: 1
 *   default: 1
 *   x: 20
 *   y: 50
 *   width: 70                     # from the left of minus to the right of plus
 *   minus: widgets/minus
 *   plus: widgets/plus
 *   minus-disabled: widgets/minus_off   # optional, shown at min
 *   plus-disabled: widgets/plus_off     # optional, shown at max
 *   text: "&lt;white&gt;%value%"          # centered between the buttons, any number costs nothing in the pack
 *   text-min: "&lt;red&gt;%value%"        # optional, the text at min
 *   text-max: "&lt;green&gt;%value%"      # optional, the text at max
 * </pre>
 * {@code minus}, {@code plus}, {@code text}, {@code text-min} and {@code text-max} are parts (see
 * {@link WidgetPart}), so each can be moved anywhere, relative to the widget:
 * <pre>
 *   minus: { glyph: my_pack/minus, glyph-disabled: my_pack/minus_off, x: 0, y: 12, hover: ["&lt;gray&gt;One less"] }
 *   plus: { glyph: my_pack/plus, x: 0, y: -12 }
 *   text: { text: "&lt;white&gt;%value%", x: 0, y: 2, width: 12, align: center }
 * </pre>
 * Plus the keys of {@link WidgetSettings}.
 */
public final class StepperWidget extends WidgetElement {

    public static final String TYPE = "stepper";
    private static final int TEXT_HEIGHT = 8;
    private static final String DEFAULT_TEXT = "<white>%value%";

    private final double min;
    private final double max;
    private final double step;
    private final int width;
    private final WidgetPart minus;
    private final WidgetPart plus;
    private final String minusDisabled;
    private final String plusDisabled;
    private final WidgetPart text;
    private final WidgetPart textMin;
    private final WidgetPart textMax;
    private final String font;

    public StepperWidget(@NotNull WidgetSettings settings, double min, double max, double step, int width, @NotNull String minus, @NotNull String plus,
                         @Nullable String minusDisabled, @Nullable String plusDisabled, @NotNull String text, @NotNull String font) {
        this(settings, min, max, step, width, WidgetPart.ofGlyph(minus), WidgetPart.ofGlyph(plus), minusDisabled, plusDisabled, WidgetPart.ofText(text),
                WidgetPart.NONE, WidgetPart.NONE, font);
    }

    public StepperWidget(@NotNull WidgetSettings settings, double min, double max, double step, int width, @NotNull WidgetPart minus,
                         @NotNull WidgetPart plus, @Nullable String minusDisabled, @Nullable String plusDisabled, @NotNull WidgetPart text,
                         @NotNull WidgetPart textMin, @NotNull WidgetPart textMax, @NotNull String font) {
        super(settings);
        this.min = min;
        this.max = max;
        this.step = step;
        this.width = width;
        this.minus = minus;
        this.plus = plus;
        this.minusDisabled = minusDisabled;
        this.plusDisabled = plusDisabled;
        this.text = text;
        this.textMin = textMin;
        this.textMax = textMax;
        this.font = font;
    }

    /**
     * @param value A value.
     * @return The value as a number between min and max.
     */
    public double current(@NotNull String value) {
        return Math.clamp(CanvasStateAction.number(value, this.min), this.min, this.max);
    }

    private static int[] size(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs, @Nullable String reference) {
        if (reference == null) return new int[]{0, 1};
        return glyphs.apply(reference).map(glyph -> new int[]{glyph.visualWidth(), glyph.height()}).orElse(new int[]{0, 1});
    }

    /**
     * The layout of the stepper, relative to its x and y.
     *
     * @param minusX     The x of minus.
     * @param minusY     The y of minus.
     * @param minusSize  The width and height of minus.
     * @param plusX      The x of plus.
     * @param plusY      The y of plus.
     * @param plusSize   The width and height of plus.
     * @param textX      The default x of the text.
     * @param textY      The default y of the text.
     * @param textWidth  The default width of the text.
     */
    private record Layout(int minusX, int minusY, int[] minusSize, int plusX, int plusY, int[] plusSize, int textX, int textY, int textWidth) {
    }

    private @NotNull Layout layout(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        int[] minusSize = size(glyphs, this.minus.glyph(null));
        int[] plusSize = size(glyphs, this.plus.glyph(null));
        int defaultPlusX = this.width - plusSize[0];
        int height = Math.max(minusSize[1], plusSize[1]);
        return new Layout(this.minus.x(0), this.minus.y(0), minusSize, this.plus.x(defaultPlusX), this.plus.y(0), plusSize, minusSize[0],
                Math.max(0, (height - TEXT_HEIGHT) / 2), Math.max(1, defaultPlusX - minusSize[0]));
    }

    private @NotNull WidgetPart textFor(double current) {
        if (current <= this.min && this.textMin.isSet()) return this.textMin.orElse(this.text);
        if (current >= this.max && this.textMax.isSet()) return this.textMax.orElse(this.text);
        return this.text;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int y = this.settings.y();
        Layout layout = this.layout(reference -> Optional.ofNullable(variants.resolve(reference)));
        this.glyph(variants, this.minus.glyph(null), y + layout.minusY());
        this.glyph(variants, this.plus.glyph(null), y + layout.plusY());
        this.glyph(variants, this.minus.glyphDisabled(this.minusDisabled), y + layout.minusY());
        this.glyph(variants, this.plus.glyphDisabled(this.plusDisabled), y + layout.plusY());
        collectTexts(variants, List.of(this.text, this.textMin.orElse(this.text), this.textMax.orElse(this.text)), DEFAULT_TEXT, y, layout.textY(), this.font);
        variants.area(y + layout.minusY(), layout.minusSize()[0], layout.minusSize()[1]);
        variants.area(y + layout.plusY(), layout.plusSize()[0], layout.plusSize()[1]);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        double current = this.current(value);
        String shown = CanvasStateAction.format(current);
        int x = this.settings.x();
        int y = this.settings.y();
        Layout layout = this.layout(context.canvasManager()::resolveGlyph);
        boolean canLower = current > this.min;
        boolean canRaise = current < this.max;

        String minusDisabledGlyph = this.minus.glyphDisabled(this.minusDisabled);
        String plusDisabledGlyph = this.plus.glyphDisabled(this.plusDisabled);
        this.glyph(context, builder, canLower || minusDisabledGlyph == null ? this.minus.glyph(null) : minusDisabledGlyph, x + layout.minusX(), y + layout.minusY());
        this.glyph(context, builder, canRaise || plusDisabledGlyph == null ? this.plus.glyph(null) : plusDisabledGlyph, x + layout.plusX(), y + layout.plusY());
        this.drawText(context, builder, this.textFor(current), DEFAULT_TEXT, shown, x, y, layout.textX(), layout.textY(), layout.textWidth(), HAlign.CENTER,
                this.font);

        if (canLower) {
            builder.area(x + layout.minusX(), y + layout.minusY(), layout.minusSize()[0], layout.minusSize()[1],
                    this.change(context, CanvasStateAction.format(Math.max(this.min, current - this.step))), this.hover(context, shown, this.minus));
        }
        if (canRaise) {
            builder.area(x + layout.plusX(), y + layout.plusY(), layout.plusSize()[0], layout.plusSize()[1],
                    this.change(context, CanvasStateAction.format(Math.min(this.max, current + this.step))), this.hover(context, shown, this.plus));
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        int right = this.settings.x() + Math.max(this.width, Math.max(this.minus.x(0), this.plus.x(0)));
        int bottom = this.settings.y() + Math.max(0, Math.max(this.minus.y(0), this.plus.y(0)));
        return outside(right, bottom, canvasWidth, canvasHeight, "the stepper");
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, CanvasStateAction.format(this.min));
        section.set("min", this.min);
        section.set("max", this.max);
        section.set("step", this.step);
        section.set("width", this.width);
        this.minus.serialize(section, "minus", true);
        this.plus.serialize(section, "plus", true);
        if (this.minusDisabled != null) section.set("minus-disabled", this.minusDisabled);
        if (this.plusDisabled != null) section.set("plus-disabled", this.plusDisabled);
        this.text.serialize(section, "text", false);
        this.textMin.serialize(section, "text-min", false);
        this.textMax.serialize(section, "text-max", false);
        if (!this.font.equals("default")) section.set("font", this.font);
    }
}
