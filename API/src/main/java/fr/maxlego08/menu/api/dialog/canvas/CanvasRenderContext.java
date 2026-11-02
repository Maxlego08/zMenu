package fr.maxlego08.menu.api.dialog.canvas;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.UnaryOperator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * What an element needs to draw itself for one player. One context is made for each canvas drawn, and the
 * placeholders it resolves are kept until the canvas is drawn, so a placeholder used by several keys is
 * parsed once.
 *
 * @param plugin        The plugin.
 * @param canvasManager The canvas manager.
 * @param player        The player the canvas is drawn for.
 * @param engine        The inventory engine to check requirements with.
 * @param metaUpdater   The text formatter.
 * @param placeholders  The placeholders of the dialog being opened.
 * @param dialog        The dialog holding the canvas, or null when it is drawn outside a dialog.
 * @param resolved      The placeholders already resolved for this canvas, by text as written, shared with the
 *                      contexts made by {@link #with(Map)} for the texts without their own placeholders.
 * @param local         The placeholders added by {@link #with(Map)}, as {@code %name%}.
 * @param localResolved The texts holding one of the {@code local} placeholders, resolved in this context only.
 */
public record CanvasRenderContext(@NotNull MenuPlugin plugin, @NotNull CanvasManager canvasManager, @NotNull Player player,
                                  @NotNull InventoryEngine engine, @NotNull PaperMetaUpdater metaUpdater,
                                  @NotNull Placeholders placeholders, @Nullable DialogInventory dialog, @NotNull Map<String, String> resolved,
                                  @NotNull Set<String> local, @NotNull Map<String, String> localResolved) {

    private static final Pattern PLACEHOLDER = Pattern.compile("%[^%\\s]+%");

    public CanvasRenderContext(@NotNull MenuPlugin plugin, @NotNull CanvasManager canvasManager, @NotNull Player player, @NotNull InventoryEngine engine,
                               @NotNull PaperMetaUpdater metaUpdater, @NotNull Placeholders placeholders, @Nullable DialogInventory dialog) {
        this(plugin, canvasManager, player, engine, metaUpdater, placeholders, dialog, new HashMap<>(), Set.of(), new HashMap<>());
    }

    /**
     * A context with more placeholders, such as {@code %value%} or {@code %entry%}. The texts without them
     * keep using the placeholders already resolved for the canvas, so a PlaceholderAPI placeholder shown by
     * every entry of a list is resolved once.
     *
     * @param values The placeholders to add, by name without {@code %}.
     * @return The context.
     */
    public @NotNull CanvasRenderContext with(@NotNull Map<String, String> values) {
        Placeholders child = this.placeholders.child();
        Set<String> names = new HashSet<>(this.local);
        values.forEach((name, value) -> {
            child.register(name, value);
            names.add("%" + name + "%");
        });
        return new CanvasRenderContext(this.plugin, this.canvasManager, this.player, this.engine, this.metaUpdater, child, this.dialog, this.resolved,
                Set.copyOf(names), new HashMap<>());
    }

    private boolean holdsLocal(@NotNull String text) {
        for (String name : this.local) {
            if (text.contains(name)) return true;
        }
        return false;
    }

    /**
     * Resolves the placeholders of a text: zMenu's, then PlaceholderAPI's. A text that is a single placeholder
     * of the dialog ({@code %canvas_name%}, an input value) is not passed to PlaceholderAPI, so a value a player
     * wrote, such as {@code %server_ip%}, is shown as written. The result is kept for the rest of this canvas.
     *
     * @param text The text as written in the configuration.
     * @return The text with its placeholders resolved.
     */
    public @NotNull String resolve(@NotNull String text) {
        if (text.indexOf('%') < 0) return text;
        Map<String, String> cache = this.holdsLocal(text) ? this.localResolved : this.resolved;
        String cached = cache.get(text);
        if (cached != null) return cached;
        String local = this.placeholders.parse(text);
        boolean ownValue = !local.equals(text) && PLACEHOLDER.matcher(text).matches();
        String value = ownValue ? local : this.plugin.parse(this.player, local);
        cache.put(text, value);
        return value;
    }

    /**
     * Resolves placeholders and formats a text for the player, with the tags of placeholder results escaped.
     *
     * @param text The text as written in the configuration.
     * @return The formatted text.
     * @see #format(String, boolean)
     */
    public @NotNull Component format(@NotNull String text) {
        return this.format(text, true);
    }

    /**
     * Resolves placeholders and formats a text for the player.
     *
     * @param text               The text as written in the configuration.
     * @param escapePlaceholders True to show the MiniMessage tags a placeholder gives as text, so a value a
     *                           player writes, such as a nickname, cannot add colors, fonts or clicks.
     * @return The formatted text.
     */
    public @NotNull Component format(@NotNull String text, boolean escapePlaceholders) {
        if (!escapePlaceholders) return this.metaUpdater.getComponent(this.resolve(text));
        return this.metaUpdater.getComponent(resolveEscaped(text, this::resolve));
    }

    /**
     * Resolves each placeholder of a text on its own and escapes the MiniMessage tags of its result; the rest
     * of the text, and placeholders that resolve to themselves, are kept as written.
     *
     * @param text     The text as written in the configuration.
     * @param resolver Resolves one placeholder, such as {@code %player_name%}.
     * @return The text, ready to be read as MiniMessage.
     */
    public static @NotNull String resolveEscaped(@NotNull String text, @NotNull UnaryOperator<String> resolver) {
        Matcher matcher = PLACEHOLDER.matcher(text);
        StringBuilder builder = new StringBuilder(text.length());
        int last = 0;
        while (matcher.find()) {
            builder.append(text, last, matcher.start());
            String placeholder = matcher.group();
            String value = resolver.apply(placeholder);
            builder.append(value.equals(placeholder) ? placeholder : MiniMessage.miniMessage().escapeTags(value));
            last = matcher.end();
        }
        builder.append(text, last, text.length());
        return builder.toString();
    }
}
