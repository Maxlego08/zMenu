package fr.maxlego08.menu.hooks.dialogs.canvas.check;

import fr.maxlego08.menu.api.button.buttons.dialogs.body.DialogCanvasBody;
import fr.maxlego08.menu.api.dialog.canvas.CanvasElement;
import fr.maxlego08.menu.api.dialog.canvas.CanvasVariants;
import fr.maxlego08.menu.api.dialog.canvas.ResolvedGlyph;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.ConditionalElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.LayoutElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.element.TextElement;
import fr.maxlego08.menu.hooks.dialogs.canvas.pack.CanvasLines;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.CanvasTextFont;
import fr.maxlego08.menu.hooks.dialogs.canvas.text.TextMeasurer;
import fr.maxlego08.menu.hooks.dialogs.canvas.widget.WidgetElement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

/**
 * Finds the mistakes of the loaded canvases while the pack is generated, so they are reported once per dialog
 * instead of when a player opens it: missing glyphs, unknown fonts, things drawn outside the canvas, fixed
 * text that does not fit its box, and the mistakes of widgets ({@link WidgetElement#problems(int, int)}), whose
 * pages and window elements are checked too.
 * <p>
 * It collects the variants of each element for the pack, and passes them on to the real request.
 */
public final class CanvasChecker implements CanvasVariants {

    private static final int TEXT_HEIGHT = 8;

    /**
     * Above this number of pack variants for one element (images, text fonts and click areas at each of their
     * positions), a warning says the template is costly.
     */
    public static final int MAX_VARIANTS = 256;

    private final CanvasVariants request;
    private final Function<String, CanvasTextFont> fonts;
    private final Function<String, Component> formatter;
    private final Map<String, Set<String>> problems = new LinkedHashMap<>();
    private final Set<String> variants = new HashSet<>();
    private final Map<String, Integer> variantCounts = new LinkedHashMap<>();

    private final Map<DialogCanvasBody, CanvasLines> lines = new IdentityHashMap<>();

    private String dialog = "";
    private String element = "";
    private int canvasHeight = -1;
    private int drawnWidth;
    private int drawnHeight;
    private int baseLine;
    private int overlayLine;
    private int overlayDepth;

    /**
     * @param request   The request every variant is passed on to.
     * @param fonts     Finds a text font by name, or gives null.
     * @param formatter Formats a text without placeholders, to measure it.
     */
    public CanvasChecker(@NotNull CanvasVariants request, @NotNull Function<String, CanvasTextFont> fonts, @NotNull Function<String, Component> formatter) {
        this.request = request;
        this.fonts = fonts;
        this.formatter = formatter;
    }

    /**
     * Collects the variants of a canvas for the pack, and checks it.
     *
     * @param dialog The name of the dialog holding the canvas.
     * @param body   The canvas.
     */
    public void check(@NotNull String dialog, @NotNull DialogCanvasBody body) {
        this.dialog = dialog;
        Integer height = body.getCanvasHeight().fixed();
        this.canvasHeight = height == null ? -1 : height;
        this.drawnWidth = body.getCanvasWidth().fixedOrFallback();
        this.drawnHeight = body.getCanvasHeight().fixedOrFallback();
        List<CanvasElement> elements = body.getElements();
        CanvasLines canvasLines = CanvasLines.of(elements, this.request::resolve, this.drawnWidth, this.drawnHeight, this.canvasHeight);
        this.lines.put(body, canvasLines);
        for (int index = 0; index < elements.size(); index++) {
            CanvasElement canvasElement = elements.get(index);
            this.element = canvasElement.name();
            this.baseLine = canvasLines.base()[index];
            this.overlayLine = canvasLines.overlay()[index];
            this.overlayDepth = 0;
            this.variants.clear();
            canvasElement.collectVariants(this);
            this.variantCounts.merge(dialog, this.variants.size(), Integer::sum);
            if (this.variants.size() > MAX_VARIANTS) {
                this.report("the pack holds it " + this.variants.size() + " times (every value at every position), more than " + MAX_VARIANTS
                        + ": the resource pack grows with each, list fewer values or positions");
            }
            this.checkText(canvasElement);
            this.checkWidget(canvasElement);
        }
    }

    private void checkWidget(@NotNull CanvasElement wrapped) {
        if (!(ConditionalElement.unwrap(wrapped) instanceof WidgetElement widget)) return;
        String prefix = widget.name().equals(this.element) ? "" : widget.name() + ": ";
        for (String problem : widget.problems(this.drawnWidth, this.drawnHeight)) this.report(prefix + problem);
        for (CanvasElement child : widget.children()) {
            this.checkText(child);
            this.checkWidget(child);
        }
    }

    /**
     * @return The problems found, by dialog, in the order the dialogs were checked.
     */
    public @NotNull Map<String, List<String>> problems() {
        Map<String, List<String>> result = new LinkedHashMap<>();
        this.problems.forEach((name, list) -> result.put(name, List.copyOf(list)));
        return Collections.unmodifiableMap(result);
    }

    /**
     * @return How many pack variants (images, text fonts and click areas at each of their positions) each
     * dialog asks for, in the order the dialogs were checked. Variants shared by elements count once each.
     */
    public @NotNull Map<String, Integer> variantCounts() {
        return Collections.unmodifiableMap(this.variantCounts);
    }

    private void report(@NotNull String problem) {
        this.problems.computeIfAbsent(this.dialog, key -> new LinkedHashSet<>()).add(this.element + ": " + problem);
    }

    private boolean outside(int top, int height) {
        return top < 0 || (this.canvasHeight >= 0 && top + height > this.canvasHeight);
    }

    /**
     * @return The text line of each element of the canvases checked, to give to each body once the pack is
     * generated ({@link DialogCanvasBody#useLines}).
     */
    public @NotNull Map<DialogCanvasBody, CanvasLines> lines() {
        return Collections.unmodifiableMap(this.lines);
    }

    private int local(int y) {
        return CanvasLines.local(y, this.overlayDepth > 0 ? this.overlayLine : this.baseLine);
    }

    @Override
    public void beginOverlay() {
        this.overlayDepth++;
    }

    @Override
    public void endOverlay() {
        this.overlayDepth = Math.max(0, this.overlayDepth - 1);
    }

    @Override
    public void glyph(@NotNull ResolvedGlyph glyph, int y) {
        int local = this.local(y);
        this.request.glyph(glyph, local);
        if (glyph.hasNativeGlyph() && local == glyph.nativeY()) return;
        this.variants.add("glyph " + glyph.reference() + "@" + glyph.height() + "@" + local);
        if (this.outside(y, glyph.height())) {
            this.report("the image " + glyph.reference() + " at y " + y + " (" + glyph.height() + " pixels high) is outside the canvas" + this.heightHint());
        }
    }

    @Override
    public void text(@NotNull String font, int y) {
        int local = this.local(y);
        this.request.text(font, local);
        this.variants.add("text " + font + "@" + local);
        if (this.fonts.apply(font) == null) this.report("the font " + font + " does not exist, add it to resource-pack/fonts.yml");
        if (this.outside(y, TEXT_HEIGHT)) this.report("a text line at y " + y + " is outside the canvas" + this.heightHint());
    }

    @Override
    public void area(int y, int width, int height) {
        this.request.area(y, width, height);
        this.variants.add("area " + width + "x" + height + "@" + y);
        if (this.outside(y, height)) this.report("the click area at y " + y + " (" + height + " pixels high) is outside the canvas" + this.heightHint());
    }

    @Override
    public @Nullable ResolvedGlyph resolve(@NotNull String reference) {
        ResolvedGlyph glyph = this.request.resolve(reference);
        if (glyph == null) this.report("the glyph " + reference + " is not found (unknown texture, or its plugin is missing)");
        return glyph;
    }

    @Override
    public int canvasWidth() {
        return this.drawnWidth;
    }

    @Override
    public int canvasHeight() {
        return this.drawnHeight;
    }

    private @NotNull String heightHint() {
        return this.canvasHeight >= 0 ? ", whose height is " + this.canvasHeight : "";
    }

    private void checkText(@NotNull CanvasElement wrapped) {
        CanvasElement canvasElement = ConditionalElement.unwrap(wrapped);
        if (canvasElement instanceof LayoutElement layout) {
            Function<String, Optional<ResolvedGlyph>> glyphs = reference -> Optional.ofNullable(this.request.resolve(reference));
            for (CanvasElement child : layout.place(glyphs)) this.checkText(child);
            return;
        }
        if (!(canvasElement instanceof TextElement text) || !text.isStatic() || text.width() == null) return;
        Integer width = text.width().fixed();
        CanvasTextFont font = this.fonts.apply(text.font().fallback());
        if (width == null || font == null || !font.isReady()) return;

        List<Component> lines = new ArrayList<>(text.lines().size());
        for (String line : text.lines()) lines.add(this.formatter.apply(line));

        TextElement.TextBox box = text.box();
        String name = canvasElement.name().equals(this.element) ? "" : " (" + canvasElement.name() + ")";
        if (box == null) {
            for (Component line : lines) {
                double measured = TextMeasurer.measure(line, font);
                if (measured > width) {
                    this.report("the text" + name + " \"" + plain(line) + "\" is " + Math.round(measured) + " pixels wide but its width is " + width + ", so it is cut");
                }
            }
            return;
        }

        int needed;
        if (box.wrap() == TextElement.Wrap.NONE) {
            needed = lines.size();
        } else {
            Component joined = Component.join(JoinConfiguration.separator(Component.newline()), lines);
            needed = TextMeasurer.wrap(joined, font, width, box.wrap() == TextElement.Wrap.WORD, Integer.MAX_VALUE, false).size();
        }
        if (needed > box.maxLines()) {
            this.report("the text" + name + " needs " + needed + " lines but max-lines is " + box.maxLines() + ", so it is cut");
        }
    }

    private static @NotNull String plain(@NotNull Component component) {
        String text = PlainTextComponentSerializer.plainText().serialize(component);
        return text.length() > 32 ? text.substring(0, 32) + "..." : text;
    }
}
