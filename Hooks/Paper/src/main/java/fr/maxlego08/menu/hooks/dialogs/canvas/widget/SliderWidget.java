package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.glyph.PanelGlyphProvider;
import fr.maxlego08.menu.hooks.dialogs.canvas.state.CanvasStateAction;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

/**
 * A number chosen by clicking along a track.
 *
 * <pre>
 * volume:
 *   type: slider
 *   min: 0
 *   max: 100
 *   step: 5
 *   default: 50
 *   x: 20
 *   y: 70
 *   width: 120
 *   track: widgets/track          # a panel texture, stretched to width x track-height
 *   track-height: 6
 *   border: 2
 *   knob: widgets/knob            # moved along the track: any value costs nothing in the pack
 *   clickable-steps: 21           # click areas along the track, each jumps to a value
 *   text: "&lt;white&gt;%value%"      # optional, right of the track
 *   text-min: "&lt;gray&gt;Off"       # optional, the text at min
 *   text-max: "&lt;red&gt;Max"        # optional, the text at max
 * </pre>
 * {@code text}, {@code text-min} and {@code text-max} are parts (see {@link WidgetPart}), moved relative to the
 * widget; {@code knob} is a glyph part whose {@code y} moves it up or down (its x follows the value). Plus the
 * keys of {@link WidgetSettings}.
 */
public final class SliderWidget extends WidgetElement {

    public static final String TYPE = "slider";
    private static final int TEXT_HEIGHT = 8;
    private static final int TEXT_GAP = 4;

    private final double min;
    private final double max;
    private final double step;
    private final int width;
    private final String track;
    private final int trackHeight;
    private final int border;
    private final WidgetPart knob;
    private final int steps;
    private final WidgetPart text;
    private final WidgetPart textMin;
    private final WidgetPart textMax;
    private final String font;

    public SliderWidget(@NotNull WidgetSettings settings, double min, double max, double step, int width, @NotNull String track, int trackHeight, int border,
                        @NotNull String knob, int steps, @Nullable String text, @NotNull String font) {
        this(settings, min, max, step, width, track, trackHeight, border, WidgetPart.ofGlyph(knob), steps, WidgetPart.ofText(text), WidgetPart.NONE,
                WidgetPart.NONE, font);
    }

    public SliderWidget(@NotNull WidgetSettings settings, double min, double max, double step, int width, @NotNull String track, int trackHeight, int border,
                        @NotNull WidgetPart knob, int steps, @NotNull WidgetPart text, @NotNull WidgetPart textMin, @NotNull WidgetPart textMax,
                        @NotNull String font) {
        super(settings);
        this.min = min;
        this.max = max;
        this.step = step;
        this.width = width;
        this.track = track;
        this.trackHeight = trackHeight;
        this.border = border;
        this.knob = knob;
        this.steps = steps;
        this.text = text;
        this.textMin = textMin;
        this.textMax = textMax;
        this.font = font;
    }

    /**
     * @param value A value.
     * @return The value as a number between min and max, on a step.
     */
    public double current(@NotNull String value) {
        return this.snap(CanvasStateAction.number(value, this.min));
    }

    private double snap(double value) {
        double snapped = this.min + Math.round((value - this.min) / this.step) * this.step;
        return Math.clamp(snapped, this.min, this.max);
    }

    /**
     * @param index The index of a click area, from 0 to clickable-steps - 1.
     * @return The value it sets.
     */
    public double valueOf(int index) {
        return this.snap(this.min + (this.max - this.min) * index / (this.steps - 1));
    }

    /**
     * @param index The index of a click area.
     * @return Its left edge, relative to the track.
     */
    public int areaStart(int index) {
        return (int) Math.round(index * (double) this.width / this.steps);
    }

    private int[] knobSize(@NotNull Function<String, Optional<ResolvedGlyph>> glyphs) {
        return glyphs.apply(this.knob.glyph("")).map(glyph -> new int[]{glyph.visualWidth(), glyph.height()}).orElse(new int[]{0, 0});
    }

    private int height(int knobHeight) {
        return Math.max(this.trackHeight, knobHeight);
    }

    private @NotNull String trackReference() {
        return PanelGlyphProvider.reference(this.track, this.width, this.trackHeight, this.border);
    }

    private @NotNull WidgetPart textFor(double current) {
        if (current <= this.min && this.textMin.isSet()) return this.textMin.orElse(this.text);
        if (current >= this.max && this.textMax.isSet()) return this.textMax.orElse(this.text);
        return this.text;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int y = this.settings.y();
        int[] knob = this.knobSize(reference -> Optional.ofNullable(variants.resolve(reference)));
        int height = this.height(knob[1]);
        this.glyph(variants, this.trackReference(), y + (height - this.trackHeight) / 2);
        this.glyph(variants, this.knob.glyph(null), y + this.knob.y((height - knob[1]) / 2));
        for (int index = 0; index < this.steps; index++) variants.area(y, this.areaStart(index + 1) - this.areaStart(index), height);
        int textY = Math.max(0, (height - TEXT_HEIGHT) / 2);
        collectTexts(variants, List.of(this.text, this.textMin.orElse(this.text), this.textMax.orElse(this.text)), null, y, textY, this.font);
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        double current = this.current(this.value(context));
        String shown = CanvasStateAction.format(current);
        int x = this.settings.x();
        int y = this.settings.y();
        int[] knob = this.knobSize(context.canvasManager()::resolveGlyph);
        int height = this.height(knob[1]);

        this.glyph(context, builder, this.trackReference(), x, y + (height - this.trackHeight) / 2);
        double fraction = this.max == this.min ? 0 : (current - this.min) / (this.max - this.min);
        int knobX = x + (int) Math.round(fraction * Math.max(0, this.width - knob[0]));
        this.glyph(context, builder, this.knob.glyph(null), knobX, y + this.knob.y((height - knob[1]) / 2));
        this.drawText(context, builder, this.textFor(current), null, shown, x, y, this.width + TEXT_GAP, Math.max(0, (height - TEXT_HEIGHT) / 2), 0,
                HAlign.LEFT, this.font);
        for (int index = 0; index < this.steps; index++) {
            double target = this.valueOf(index);
            int start = this.areaStart(index);
            builder.area(x + start, y, this.areaStart(index + 1) - start, height,
                    target == current ? null : this.change(context, CanvasStateAction.format(target)),
                    this.hover(context, CanvasStateAction.format(target), this.knob));
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        return outside(this.settings.x() + this.width, this.settings.y() + this.trackHeight, canvasWidth, canvasHeight, "the slider");
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, CanvasStateAction.format(this.min));
        section.set("min", this.min);
        section.set("max", this.max);
        section.set("step", this.step);
        section.set("width", this.width);
        section.set("track", this.track);
        section.set("track-height", this.trackHeight);
        section.set("border", this.border);
        this.knob.serialize(section, "knob", true);
        section.set("clickable-steps", this.steps);
        this.text.serialize(section, "text", false);
        this.textMin.serialize(section, "text-min", false);
        this.textMax.serialize(section, "text-max", false);
        if (!this.font.equals("default")) section.set("font", this.font);
    }
}
