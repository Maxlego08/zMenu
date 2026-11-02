package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.VisibleCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * Several options at once: each option is a checkbox, its value the options checked, in the order of the
 * options, separated by commas ({@code pvp,shop}).
 *
 * <pre>
 * tags:
 *   type: checklist
 *   default: "pvp"               # the options checked at first, comma separated
 *   x: 8
 *   y: 20
 *   direction: column            # column (default) or row
 *   height: 10
 *   gap: 3
 *   max: 2                       # optional, the most options checked at once
 *   options:
 *     pvp: { label: "PvP", width: 60 }
 *     shop: { label: "Shop", width: 60 }
 *   glyph-on: widgets/checkbox_on
 *   glyph-off: widgets/checkbox_off
 * </pre>
 * Like tabs and radio, an option with {@code x} or {@code y} (relative to the widget) is placed on its own, and
 * an option can set {@code label-checked}, {@code glyph-on}, {@code glyph-off} and {@code hover}. The
 * {@code label} and {@code label-checked} parts (see {@link WidgetPart}) place the label inside each option.
 * Read it with {@code visible: "%canvas_tags% has pvp"}. Plus the keys of {@link WidgetSettings}.
 */
public final class ChecklistWidget extends WidgetElement {

    public static final String TYPE = "checklist";
    private static final int TEXT_HEIGHT = 8;
    private static final int LABEL_GAP = 4;

    private final List<ChoiceWidget.Option> options;
    private final boolean column;
    private final int gap;
    private final int height;
    private final int max;
    private final String glyphOn;
    private final String glyphOff;
    private final String font;
    private final WidgetPart label;
    private final WidgetPart labelChecked;
    private final int[][] positions;

    public ChecklistWidget(@NotNull WidgetSettings settings, @NotNull List<ChoiceWidget.Option> options, boolean column, int gap, int height, int max,
                           @NotNull String glyphOn, @NotNull String glyphOff, @NotNull String font) {
        this(settings, options, column, gap, height, max, glyphOn, glyphOff, font, WidgetPart.NONE, WidgetPart.NONE);
    }

    public ChecklistWidget(@NotNull WidgetSettings settings, @NotNull List<ChoiceWidget.Option> options, boolean column, int gap, int height, int max,
                           @NotNull String glyphOn, @NotNull String glyphOff, @NotNull String font, @NotNull WidgetPart label,
                           @NotNull WidgetPart labelChecked) {
        super(settings);
        this.options = List.copyOf(options);
        this.column = column;
        this.gap = gap;
        this.height = height;
        this.max = max;
        this.glyphOn = glyphOn;
        this.glyphOff = glyphOff;
        this.font = font;
        this.label = label;
        this.labelChecked = labelChecked;
        this.positions = ChoiceWidget.positions(this.options, column, gap, height);
    }

    /**
     * @param value A value.
     * @return The options it checks, in the order of the options.
     */
    public @NotNull Set<String> checked(@NotNull String value) {
        Set<String> checked = new LinkedHashSet<>();
        for (ChoiceWidget.Option option : this.options) {
            if (VisibleCondition.hasItem(value, option.id())) checked.add(option.id());
        }
        return checked;
    }

    /**
     * @param checked The options checked.
     * @param id      The option clicked.
     * @return The value after the click, or null when the click is refused (max reached).
     */
    public String toggled(@NotNull Set<String> checked, @NotNull String id) {
        boolean adding = !checked.contains(id);
        if (adding && this.max > 0 && checked.size() >= this.max) return null;
        List<String> next = new ArrayList<>();
        for (ChoiceWidget.Option option : this.options) {
            boolean on = option.id().equals(id) ? adding : checked.contains(option.id());
            if (on) next.add(option.id());
        }
        return String.join(",", next);
    }

    private int optionX(int index) {
        return this.settings.x() + this.positions[index][0];
    }

    private int optionY(int index) {
        return this.settings.y() + this.positions[index][1];
    }

    private @NotNull String look(@NotNull ChoiceWidget.Option option, boolean on) {
        String own = on ? option.lookSelected() : option.lookIdle();
        return own == null ? (on ? this.glyphOn : this.glyphOff) : own;
    }

    private @NotNull WidgetPart labelPart(boolean on) {
        return on ? this.labelChecked.orElse(this.label) : this.label;
    }

    @Override
    public void collectVariants(@NotNull CanvasVariants variants) {
        int textY = Math.max(0, (this.height - TEXT_HEIGHT) / 2);
        for (int index = 0; index < this.options.size(); index++) {
            ChoiceWidget.Option option = this.options.get(index);
            int y = this.optionY(index);
            this.glyph(variants, this.look(option, true), y);
            this.glyph(variants, this.look(option, false), y);
            collectTexts(variants, List.of(this.labelPart(false), this.labelPart(true)), option.label(), y, textY, this.font);
            variants.area(y, option.width(), this.height);
        }
    }

    @Override
    public void render(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder) {
        String value = this.value(context);
        Set<String> checked = this.checked(value);
        int textY = Math.max(0, (this.height - TEXT_HEIGHT) / 2);
        for (int index = 0; index < this.options.size(); index++) {
            ChoiceWidget.Option option = this.options.get(index);
            boolean on = checked.contains(option.id());
            int x = this.optionX(index);
            int y = this.optionY(index);
            String reference = this.look(option, on);
            this.glyph(context, builder, reference, x, y);
            int glyphWidth = context.canvasManager().resolveGlyph(reference).map(ResolvedGlyph::visualWidth).orElse(0);
            this.drawText(context, builder, this.labelPart(on).withText(option.label(on)), null, option.id(), x, y, glyphWidth + LABEL_GAP, textY, 0,
                    HAlign.LEFT, this.font);
            String next = this.toggled(checked, option.id());
            builder.area(x, y, option.width(), this.height, next == null ? null : this.change(context, next), this.hover(context, value, option.hover()));
        }
    }

    @Override
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        int right = 0;
        int bottom = 0;
        for (int index = 0; index < this.options.size(); index++) {
            right = Math.max(right, this.optionX(index) + this.options.get(index).width());
            bottom = Math.max(bottom, this.optionY(index) + this.height);
        }
        List<String> problems = new ArrayList<>(outside(right, bottom, canvasWidth, canvasHeight, "the options"));
        long flowing = this.options.stream().filter(option -> !option.isPlaced()).count();
        if (this.column && flowing > 1 && this.height + this.gap < CLICK_ROW) {
            problems.add("its options are " + (this.height + this.gap) + " pixels apart: clients before 1.21.11 find a click by its line of "
                    + CLICK_ROW + " pixels and may tick the wrong option (height + gap of " + CLICK_ROW + " or more)");
        }
        return problems;
    }

    @Override
    public void serialize(@NotNull ConfigurationSection section) {
        this.settings.serialize(section, TYPE, "");
        if (!this.column) section.set("direction", "row");
        if (this.gap != 2) section.set("gap", this.gap);
        section.set("height", this.height);
        if (this.max > 0) section.set("max", this.max);
        ConfigurationSection options = section.createSection("options");
        for (ChoiceWidget.Option option : this.options) option.serialize(options, "glyph-on", "glyph-off", "label-checked");
        section.set("glyph-on", this.glyphOn);
        section.set("glyph-off", this.glyphOff);
        if (!this.font.equals("default")) section.set("font", this.font);
        this.label.serialize(section, "label", false);
        this.labelChecked.serialize(section, "label-checked", false);
    }
}
