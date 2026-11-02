package fr.maxlego08.menu.hooks.dialogs.canvas.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.TextComponent.Builder;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextDecoration;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Measures text the way the client draws it: each character moves the cursor by its advance, and bold
 * moves it one more pixel.
 * <p>
 * Only text components are measured. Translatable, keybind or score components are drawn by the client
 * with text the server does not know, so they count as empty.
 */
public final class TextMeasurer {

    private static final String ELLIPSIS = "...";

    private TextMeasurer() {
    }

    /**
     * @param text The text.
     * @param font The font it is drawn with.
     * @return Its width in GUI pixels.
     */
    public static double measure(@NotNull Component text, @NotNull CanvasTextFont font) {
        double width = 0;
        for (Leaf leaf : leaves(text)) width += measure(leaf.text(), leaf.style(), font);
        return width;
    }

    private static double measure(@NotNull String text, @NotNull Style style, @NotNull CanvasTextFont font) {
        boolean bold = style.decoration(TextDecoration.BOLD) == TextDecoration.State.TRUE;
        double width = 0;
        for (int index = 0; index < text.length(); ) {
            int codepoint = text.codePointAt(index);
            width += font.advance(codepoint) + (bold ? 1 : 0);
            index += Character.charCount(codepoint);
        }
        return width;
    }

    /**
     * Cuts a text so it fits a width, ending it with an ellipsis.
     *
     * @param text     The text.
     * @param font     The font it is drawn with.
     * @param maxWidth The width it must fit.
     * @return The text unchanged if it fits, else the cut text.
     */
    public static @NotNull Component fit(@NotNull Component text, @NotNull CanvasTextFont font, double maxWidth) {
        List<Leaf> leaves = leaves(text);
        double total = 0;
        for (Leaf leaf : leaves) total += measure(leaf.text(), leaf.style(), font);
        if (total <= maxWidth) return text;
        return cut(leaves, font, maxWidth);
    }

    /**
     * Ends a text with an ellipsis, cutting it so the whole fits a width.
     *
     * @param text     The text.
     * @param font     The font it is drawn with.
     * @param maxWidth The width it must fit.
     * @return The text followed by an ellipsis.
     */
    public static @NotNull Component withEllipsis(@NotNull Component text, @NotNull CanvasTextFont font, double maxWidth) {
        return cut(leaves(text), font, maxWidth);
    }

    /**
     * Breaks a text into lines that fit a width.
     * <p>
     * In word mode, a line breaks at its last space, and a word wider than the width is broken where it
     * passes it. In character mode, a line breaks at the character that passes the width. A line feed always
     * breaks. Spaces at the end of a line and at the start of a broken line are dropped, and styles carry on
     * to the next line.
     *
     * @param text     The text.
     * @param font     The font it is drawn with.
     * @param maxWidth The width of a line.
     * @param words    True to break between words, false to break between characters.
     * @param maxLines The number of lines kept.
     * @param ellipsis True to end the last kept line with an ellipsis when lines were dropped.
     * @return The lines, at least one.
     */
    public static @NotNull List<Component> wrap(@NotNull Component text, @NotNull CanvasTextFont font, double maxWidth, boolean words, int maxLines,
                                                boolean ellipsis) {
        List<List<StyledChar>> lines = new ArrayList<>();
        List<StyledChar> current = new ArrayList<>();
        double width = 0;
        int lastSpace = -1;
        boolean wrapped = false;

        for (StyledChar character : characters(text)) {
            if (character.codepoint() == '\n') {
                lines.add(current);
                current = new ArrayList<>();
                width = 0;
                lastSpace = -1;
                wrapped = false;
                continue;
            }
            if (character.codepoint() == ' ' && current.isEmpty() && wrapped) continue;

            double advance = advance(character, font);
            if (width + advance > maxWidth && !current.isEmpty()) {
                if (character.codepoint() == ' ') {
                    lines.add(current);
                    current = new ArrayList<>();
                    width = 0;
                    lastSpace = -1;
                    wrapped = true;
                    continue;
                }
                if (words && lastSpace >= 0) {
                    List<StyledChar> rest = new ArrayList<>(current.subList(lastSpace + 1, current.size()));
                    lines.add(new ArrayList<>(current.subList(0, lastSpace)));
                    current = rest;
                    width = advance(current, font);
                } else {
                    lines.add(current);
                    current = new ArrayList<>();
                    width = 0;
                }
                lastSpace = -1;
                wrapped = true;
                if (width + advance > maxWidth && !current.isEmpty()) {
                    lines.add(current);
                    current = new ArrayList<>();
                    width = 0;
                }
            }
            current.add(character);
            width += advance;
            if (character.codepoint() == ' ') lastSpace = current.size() - 1;
        }
        lines.add(current);

        List<Component> result = new ArrayList<>();
        for (List<StyledChar> line : lines) result.add(component(trimEnd(line)));
        if (result.size() > maxLines) {
            List<Component> kept = new ArrayList<>(result.subList(0, Math.max(1, maxLines)));
            if (ellipsis) kept.set(kept.size() - 1, withEllipsis(kept.getLast(), font, maxWidth));
            return kept;
        }
        return result;
    }

    private static @NotNull List<StyledChar> characters(@NotNull Component text) {
        List<StyledChar> characters = new ArrayList<>();
        for (Leaf leaf : leaves(text)) {
            String content = leaf.text();
            for (int index = 0; index < content.length(); ) {
                int codepoint = content.codePointAt(index);
                characters.add(new StyledChar(codepoint, leaf.style()));
                index += Character.charCount(codepoint);
            }
        }
        return characters;
    }

    private static double advance(@NotNull StyledChar character, @NotNull CanvasTextFont font) {
        return measure(Character.toString(character.codepoint()), character.style(), font);
    }

    private static double advance(@NotNull List<StyledChar> characters, @NotNull CanvasTextFont font) {
        double width = 0;
        for (StyledChar character : characters) width += advance(character, font);
        return width;
    }

    private static @NotNull List<StyledChar> trimEnd(@NotNull List<StyledChar> line) {
        int end = line.size();
        while (end > 0 && line.get(end - 1).codepoint() == ' ') end--;
        return line.subList(0, end);
    }

    private static @NotNull Component component(@NotNull List<StyledChar> line) {
        Builder builder = Component.text();
        StringBuilder run = new StringBuilder();
        Style style = null;
        for (StyledChar character : line) {
            if (style != null && !style.equals(character.style())) {
                builder.append(Component.text(run.toString(), style));
                run.setLength(0);
            }
            style = character.style();
            run.appendCodePoint(character.codepoint());
        }
        if (style != null && !run.isEmpty()) builder.append(Component.text(run.toString(), style));
        return builder.build();
    }

    private static @NotNull Component cut(@NotNull List<Leaf> leaves, @NotNull CanvasTextFont font, double maxWidth) {
        Builder builder = Component.text();
        double width = 0;
        Style lastStyle = Style.empty();
        for (Leaf leaf : leaves) {
            double ellipsis = measure(ELLIPSIS, leaf.style(), font);
            StringBuilder kept = new StringBuilder();
            boolean full = false;
            for (int index = 0; index < leaf.text().length(); ) {
                int codepoint = leaf.text().codePointAt(index);
                double advance = measure(Character.toString(codepoint), leaf.style(), font);
                if (width + advance + ellipsis > maxWidth) {
                    full = true;
                    break;
                }
                kept.appendCodePoint(codepoint);
                width += advance;
                index += Character.charCount(codepoint);
            }
            if (!kept.isEmpty()) builder.append(Component.text(kept.toString(), leaf.style()));
            lastStyle = leaf.style();
            if (full) break;
        }
        builder.append(Component.text(ELLIPSIS, lastStyle));
        return builder.build();
    }

    private static @NotNull List<Leaf> leaves(@NotNull Component component) {
        List<Leaf> leaves = new ArrayList<>();
        collect(component, Style.empty(), leaves);
        return leaves;
    }

    private static void collect(@NotNull Component component, @NotNull Style parent, @NotNull List<Leaf> leaves) {
        Style style = component.style().merge(parent, Style.Merge.Strategy.IF_ABSENT_ON_TARGET);
        if (component instanceof TextComponent textComponent && !textComponent.content().isEmpty()) {
            leaves.add(new Leaf(textComponent.content(), style));
        }
        for (Component child : component.children()) collect(child, style, leaves);
    }

    private record Leaf(@NotNull String text, @NotNull Style style) {
    }

    private record StyledChar(int codepoint, @NotNull Style style) {
    }
}
