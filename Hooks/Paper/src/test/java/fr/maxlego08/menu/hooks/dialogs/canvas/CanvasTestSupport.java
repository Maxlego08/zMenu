package fr.maxlego08.menu.hooks.dialogs.canvas;

import fr.maxlego08.menu.zcore.logger.Logger;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Helpers shared by the canvas tests.
 */
public final class CanvasTestSupport {

    public static final List<String> LOGS = new ArrayList<>();

    private CanvasTestSupport() {
    }

    /**
     * Installs a logger that keeps messages in {@link #LOGS}, since zMenu's logger needs a running plugin.
     */
    public static void installLogger() {
        if (Logger.getLogger() != null) return;
        new Logger("test") {
            @Override
            public void log(@NotNull String message, @NotNull LogType type, @NotNull Object... args) {
                LOGS.add(message);
            }
        };
    }

    /**
     * Adds up how far the characters of a component move the cursor.
     *
     * @param component The component.
     * @param advances  The advance of every character that is not a space character of zMenu.
     * @return The sum of the advances.
     */
    public static double netAdvance(@NotNull Component component, @NotNull Map<Integer, Double> advances) {
        double total = 0;
        if (component instanceof TextComponent text) {
            String content = text.content();
            for (int index = 0; index < content.length(); ) {
                int codepoint = content.codePointAt(index);
                total += spaceAdvance(codepoint, advances);
                index += Character.charCount(codepoint);
            }
        }
        for (Component child : component.children()) total += netAdvance(child, advances);
        return total;
    }

    /**
     * The furthest the cursor goes while a component is drawn, as the client's line splitting sees it.
     *
     * @param component The component.
     * @param advances  The advance of every character that is not a space character of zMenu.
     * @return The furthest position.
     */
    public static double peakAdvance(@NotNull Component component, @NotNull Map<Integer, Double> advances) {
        double[] cursorAndPeak = new double[2];
        peak(component, advances, cursorAndPeak);
        return cursorAndPeak[1];
    }

    private static void peak(@NotNull Component component, @NotNull Map<Integer, Double> advances, double[] cursorAndPeak) {
        if (component instanceof TextComponent text) {
            String content = text.content();
            for (int index = 0; index < content.length(); ) {
                int codepoint = content.codePointAt(index);
                cursorAndPeak[0] += spaceAdvance(codepoint, advances);
                cursorAndPeak[1] = Math.max(cursorAndPeak[1], cursorAndPeak[0]);
                index += Character.charCount(codepoint);
            }
        }
        for (Component child : component.children()) peak(child, advances, cursorAndPeak);
    }

    private static double spaceAdvance(int codepoint, @NotNull Map<Integer, Double> advances) {
        if (codepoint >= 0xFFF00 && codepoint <= 0xFFF0A) return 1 << (codepoint - 0xFFF00);
        if (codepoint >= 0xFFF10 && codepoint <= 0xFFF1A) return -(1 << (codepoint - 0xFFF10));
        return switch (codepoint) {
            case 0xFFF20 -> 0.5;
            case 0xFFF21 -> -0.5;
            case 0xFFF22 -> 0.25;
            case 0xFFF23 -> -0.25;
            default -> advances.getOrDefault(codepoint, 0.0);
        };
    }
}
