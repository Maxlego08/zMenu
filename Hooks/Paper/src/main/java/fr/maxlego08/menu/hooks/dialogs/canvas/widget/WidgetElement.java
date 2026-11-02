package fr.maxlego08.menu.hooks.dialogs.canvas.widget;

import fr.maxlego08.menu.api.dialog.canvas.CanvasBuilder;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasRenderContext;
import fr.maxlego08.menu.api.dialog.canvas.CanvasStateService;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.HAlign;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A canvas element with a state: a set of values, click areas that change the value, and visuals chosen by
 * the value. A click stores the new value in the canvas state, runs {@code on-change}, then opens the dialog
 * again so the change shows.
 * <p>
 * Every image a widget can show is declared in {@link #collectVariants(CanvasVariants)}, since the pack is
 * generated before any player opens the dialog.
 */
public abstract class WidgetElement implements CanvasElement {

    protected final WidgetSettings settings;

    protected WidgetElement(@NotNull WidgetSettings settings) {
        this.settings = settings;
    }

    @Override
    public @NotNull String name() {
        return this.settings.name();
    }

    @Override
    public boolean isClickable() {
        return true;
    }

    @Override
    public boolean isStatic() {
        return false;
    }

    /**
     * The smallest distance between two rows of click areas that clients before 1.21.11 can tell apart: they
     * find a click by its line of 9 pixels.
     */
    public static final int CLICK_ROW = 9;

    /**
     * The mistakes of this widget in a canvas, reported once when the dialogs load.
     *
     * @param canvasWidth  The width of the canvas.
     * @param canvasHeight The height of the canvas.
     * @return The problems, empty when there are none.
     */
    public @NotNull List<String> problems(int canvasWidth, int canvasHeight) {
        return List.of();
    }

    /**
     * @return The elements this widget holds (pages, window elements), checked like the elements of the canvas.
     */
    public @NotNull List<CanvasElement> children() {
        return List.of();
    }

    /**
     * @param right       The right edge of the widget.
     * @param bottom      The bottom edge of the widget.
     * @param canvasWidth  The width of the canvas.
     * @param canvasHeight The height of the canvas.
     * @param what        What goes past, for the message.
     * @return The problems of a box going past the canvas.
     */
    protected static @NotNull List<String> outside(int right, int bottom, int canvasWidth, int canvasHeight, @NotNull String what) {
        List<String> problems = new ArrayList<>();
        if (right > canvasWidth) problems.add(what + " goes past the right edge of the canvas (to x " + right + ", the canvas is " + canvasWidth + " wide)");
        if (bottom > canvasHeight) problems.add(what + " goes past the bottom of the canvas (to y " + bottom + ", the canvas is " + canvasHeight + " high)");
        return problems;
    }

    /**
     * @return The common settings.
     */
    public @NotNull WidgetSettings settings() {
        return this.settings;
    }

    /**
     * @param context The player.
     * @return The current value: the stored one, else the default with its placeholders resolved.
     */
    protected @NotNull String value(@NotNull CanvasRenderContext context) {
        DialogInventory dialog = context.dialog();
        if (dialog != null) {
            String stored = context.canvasManager().getStateService().get(context.player().getUniqueId(), dialog.getFileName(), this.settings.key());
            if (stored != null) return stored;
        }
        return context.resolve(this.settings.defaultValue()).trim();
    }

    /**
     * @param context The player.
     * @return True if the widget takes clicks for the player.
     */
    protected boolean isEnabled(@NotNull CanvasRenderContext context) {
        return this.settings.enabled() == null || this.settings.enabled().test(context);
    }

    /**
     * Formats a text of the widget, with {@code %value%} replaced by a value.
     *
     * @param context The player.
     * @param text    The text as written.
     * @param value   The value.
     * @return The text.
     */
    protected @NotNull Component text(@NotNull CanvasRenderContext context, @NotNull String text, @NotNull String value) {
        return context.with(Map.of("value", value)).format(text, this.settings.escapePlaceholders());
    }

    /**
     * @param context The player.
     * @param value   The current value.
     * @return The tooltip, or null.
     */
    protected @Nullable Component hover(@NotNull CanvasRenderContext context, @NotNull String value) {
        if (this.settings.hover().isEmpty()) return null;
        List<Component> lines = new ArrayList<>(this.settings.hover().size());
        for (String line : this.settings.hover()) lines.add(this.text(context, line, value));
        return Component.join(JoinConfiguration.newlines(), lines);
    }

    /**
     * @param context The player.
     * @param value   The value, for {@code %value%}.
     * @param part    A clickable part.
     * @return The tooltip of the part when it has one, else the widget's.
     */
    protected @Nullable Component hover(@NotNull CanvasRenderContext context, @NotNull String value, @NotNull WidgetPart part) {
        return this.hover(context, value, part.hover());
    }

    /**
     * @param context The player.
     * @param value   The value, for {@code %value%}.
     * @param lines   Tooltip lines, empty for the widget's {@code hover}.
     * @return The tooltip, or null.
     */
    protected @Nullable Component hover(@NotNull CanvasRenderContext context, @NotNull String value, @NotNull List<String> lines) {
        if (lines.isEmpty()) return this.hover(context, value);
        List<Component> components = new ArrayList<>(lines.size());
        for (String line : lines) components.add(this.text(context, line, value));
        return Component.join(JoinConfiguration.newlines(), components);
    }

    /**
     * Draws the text of a part, where the part says or else where the widget puts it.
     *
     * @param context      The player.
     * @param builder      The canvas.
     * @param part         The part.
     * @param fallbackText The widget's text, or null.
     * @param value        The value, for {@code %value%}.
     * @param originX      What the part's x is relative to (the widget, or one of its options).
     * @param originY      What the part's y is relative to.
     * @param defaultX     The x the widget puts it at, relative to the origin.
     * @param defaultY     The y the widget puts it at, relative to the origin.
     * @param defaultWidth The width of its box, 0 to draw it as it is.
     * @param defaultAlign Its alignment in the box.
     * @param defaultFont  Its font.
     */
    protected void drawText(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder, @NotNull WidgetPart part, @Nullable String fallbackText,
                            @NotNull String value, int originX, int originY, int defaultX, int defaultY, int defaultWidth, @NotNull HAlign defaultAlign,
                            @NotNull String defaultFont) {
        String text = part.text(fallbackText);
        if (text == null) return;
        int x = originX + part.x(defaultX);
        int y = originY + part.y(defaultY);
        int width = part.width(defaultWidth);
        Component component = this.text(context, text, value);
        if (width > 0) {
            builder.text(component, part.font(defaultFont), x, y, width, part.align(defaultAlign));
        } else {
            builder.text(component, part.font(defaultFont), x, y);
        }
    }

    /**
     * Declares the text of a part for the pack.
     *
     * @param variants     The collector.
     * @param part         The part.
     * @param fallbackText The widget's text, or null.
     * @param originY      What the part's y is relative to.
     * @param defaultY     The y the widget puts it at, relative to the origin.
     * @param defaultFont  Its font.
     */
    protected static void collectText(@NotNull CanvasVariants variants, @NotNull WidgetPart part, @Nullable String fallbackText, int originY, int defaultY,
                                      @NotNull String defaultFont) {
        collectTexts(variants, List.of(part), fallbackText, originY, defaultY, defaultFont);
    }

    /**
     * Declares the texts of several parts for the pack (the states of one text), each font and y once.
     *
     * @param variants     The collector.
     * @param parts        The parts.
     * @param fallbackText The widget's text, or null.
     * @param originY      What the parts' y is relative to.
     * @param defaultY     The y the widget puts them at, relative to the origin.
     * @param defaultFont  Their font.
     */
    protected static void collectTexts(@NotNull CanvasVariants variants, @NotNull List<WidgetPart> parts, @Nullable String fallbackText, int originY,
                                       int defaultY, @NotNull String defaultFont) {
        Set<String> declared = new HashSet<>();
        for (WidgetPart part : parts) {
            if (part.text(fallbackText) == null) continue;
            String font = part.font(defaultFont);
            int y = originY + part.y(defaultY);
            if (declared.add(font + "@" + y)) variants.text(font, y);
        }
    }

    /**
     * The click event that changes the value, or null when the widget is disabled for the player.
     *
     * @param context The player.
     * @param value   The value a click sets.
     * @return The click event, or null.
     */
    protected @Nullable ClickEvent change(@NotNull CanvasRenderContext context, @NotNull String value) {
        if (!this.isEnabled(context)) return null;
        Map<String, String> values = new LinkedHashMap<>();
        values.put(this.settings.key(), value);
        return this.update(context, values, value);
    }

    /**
     * The click event that sets several state keys at once, then opens the dialog again.
     *
     * @param context The player.
     * @param values  The keys to set, a null value removes the key.
     * @param changed The new value of the widget, which runs {@code on-change}, or null when the value does
     *                not change (the dropdown opening, for example).
     * @return The click event.
     */
    protected @NotNull ClickEvent update(@NotNull CanvasRenderContext context, @NotNull Map<String, String> values, @Nullable String changed) {
        String previous = this.value(context);
        Map<String, String> copy = new LinkedHashMap<>(values);
        return context.canvasManager().callback(context.player(), clicker -> this.apply(context, clicker, copy, previous, changed));
    }

    /**
     * @param context The player.
     * @param key     A state key.
     * @return Its value, or null when it is not set.
     */
    protected @Nullable String stored(@NotNull CanvasRenderContext context, @NotNull String key) {
        DialogInventory dialog = context.dialog();
        return dialog == null ? null : context.canvasManager().getStateService().get(context.player().getUniqueId(), dialog.getFileName(), key);
    }

    private void apply(@NotNull CanvasRenderContext context, @NotNull Player player, @NotNull Map<String, String> values, @NotNull String previous,
                       @Nullable String changed) {
        DialogInventory dialog = context.dialog();
        if (dialog == null) return;
        CanvasStateService state = context.canvasManager().getStateService();
        values.forEach((key, value) -> {
            if (value == null) {
                state.remove(player.getUniqueId(), dialog.getFileName(), key);
            } else {
                state.set(player.getUniqueId(), dialog.getFileName(), key, value, this.settings.keep());
            }
        });

        if (changed != null) {
            Placeholders placeholders = context.placeholders().child();
            placeholders.register("value", changed);
            placeholders.register("previous", previous);
            for (Action action : this.settings.onChange()) action.preExecute(player, null, context.engine(), placeholders);
        }

        context.canvasManager().reopen(player, dialog.getPlugin().getName() + ":" + dialog.getFileName());
    }

    /**
     * The tallest click area, a hitbox glyph cell.
     */
    protected static final int MAX_AREA_HEIGHT = 256;

    /**
     * Adds a click area over the whole canvas, in strips of {@link #MAX_AREA_HEIGHT} pixels. The client gives
     * a click to the last area with a click event under the cursor, so this area takes every click on the
     * elements drawn before it.
     *
     * @param builder The canvas.
     * @param click   What a click does: {@link #nothing(CanvasRenderContext)} to only stop the clicks.
     */
    protected static void coverCanvas(@NotNull CanvasBuilder builder, @NotNull ClickEvent click) {
        for (int top = 0; top < builder.height(); top += MAX_AREA_HEIGHT) {
            builder.area(0, top, builder.width(), Math.min(MAX_AREA_HEIGHT, builder.height() - top), click, null);
        }
    }

    /**
     * @param context The player.
     * @return A click event that does nothing, to stop clicks from reaching what is under an area.
     */
    protected static @NotNull ClickEvent nothing(@NotNull CanvasRenderContext context) {
        return context.canvasManager().callback(context.player(), player -> {
        });
    }

    /**
     * Declares the click areas of {@link #coverCanvas(CanvasBuilder, ClickEvent)}.
     *
     * @param variants The collector.
     */
    protected static void coverCanvas(@NotNull CanvasVariants variants) {
        int width = variants.canvasWidth();
        int height = variants.canvasHeight();
        if (width < 1 || height < 1) return;
        for (int top = 0; top < height; top += MAX_AREA_HEIGHT) variants.area(top, width, Math.min(MAX_AREA_HEIGHT, height - top));
    }

    /**
     * Draws a glyph when it exists.
     *
     * @param context   The player.
     * @param builder   The canvas.
     * @param reference The glyph reference.
     * @param x         Its x.
     * @param y         Its y.
     */
    protected void glyph(@NotNull CanvasRenderContext context, @NotNull CanvasBuilder builder, @Nullable String reference, int x, int y) {
        if (reference == null) return;
        context.canvasManager().resolveGlyph(reference).ifPresent(glyph -> builder.glyph(glyph, x, y));
    }

    /**
     * Declares a glyph at a y for the pack, when it exists.
     *
     * @param variants  The collector.
     * @param reference The glyph reference, or null.
     * @param y         Its y.
     * @return The glyph, or null.
     */
    protected @Nullable ResolvedGlyph glyph(@NotNull CanvasVariants variants, @Nullable String reference, int y) {
        if (reference == null) return null;
        ResolvedGlyph glyph = variants.resolve(reference);
        if (glyph != null) variants.glyph(glyph, y);
        return glyph;
    }
}
