package fr.maxlego08.menu.api.text;

import fr.maxlego08.menu.api.placeholder.LocalPlaceholder;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.*;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The MiniMessageFlow instance zMenu formats all its text with, and what configures it.
 *
 * <p>zMenu builds its implementation when the plugin is created, through MiniMessageFlow's Paper integration, and
 * installs it ({@link #install}); {@link #get()} gives it. Before that (unit tests, code running before zMenu is
 * created) {@link #get()} gives a plain instance with the same options and the same {@link #KEYS}, so values made with
 * one render with the other.</p>
 *
 * <p>MiniMessageFlow's classes are relocated into zMenu ({@code fr.maxlego08.menu.hooks.minimessageflow}); the API jar
 * carries the same relocated classes.</p>
 */
public interface TextService {

    /**
     * The placeholder keys of every instance zMenu builds: {@code Values} made by one are read by another.
     */
    @NotNull ValueKeys KEYS = ValueKeys.builder().build();

    /**
     * The prefix of zMenu's own placeholders, {@code %zmenu_...%}.
     */
    @NotNull String ZMENU_PREFIX = "zmenu_";

    /**
     * @return the installed service, or a plain one with the same options before zMenu installs its own
     */
    static @NotNull TextService get() {
        return TextServices.get();
    }

    /**
     * Sets the service {@link #get()} gives. Called by zMenu when the plugin is created.
     *
     * @param service the service
     */
    static void install(@NotNull TextService service) {
        TextServices.install(service);
    }

    /**
     * What every instance zMenu builds has, set on its builder: {@link #KEYS}, the old modifier prefixes
     * ({@code %upper_name%}), the empty text for a modifier that does not apply (as zMenu always did), PlaceholderAPI
     * placeholders read inside formatted values, {@code %player%} as the viewer's name, zMenu's own
     * {@code %zmenu_...%} placeholders.
     *
     * @param builder the builder of an instance
     */
    static void options(@NotNull MiniMessageFlow.Builder builder) {
        builder.keys(KEYS)
                .legacyPrefixModifiers(true)
                .failedModifier(FailedModifier.EMPTY)
                .placeholdersInFormattedValues(true)
                .resolver(TextService::player)
                .resolver(TextService::zmenu);
    }

    /**
     * The root style of a line of item text (a display name, a lore line): not italic, unless the text asks for italic
     * itself ({@code &o}, {@code <i>}, {@code <em>}, {@code <italic>}), as the client shows item text in italic by
     * default and zMenu always turned it off. Give it to {@code Template.style}.
     *
     * @param text the line as written
     * @return the style
     */
    static @NotNull Style itemLineStyle(@NotNull String text) {
        boolean italic = text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>");
        return italic ? TextServices.ITALIC : TextServices.NOT_ITALIC;
    }

    /**
     * Compiles a line of item text (a display name) with its {@link #itemLineStyle}.
     *
     * @param text the line as written
     * @return the template
     */
    static @NotNull Template compileItemLine(@NotNull String text) {
        return get().flow().compile(text).style(itemLineStyle(text));
    }

    /**
     * Compiles lore lines, each with its {@link #itemLineStyle}. A line holding {@code \n} gives several lines.
     *
     * @param lines the lines as written
     * @return the templates
     */
    static @NotNull TemplateList compileItemLines(@NotNull List<String> lines) {
        List<String> split = new ArrayList<>(lines.size());
        for (String line : lines) {
            if (line.indexOf('\n') < 0) {
                split.add(line);
            } else {
                Collections.addAll(split, line.split("\n"));
            }
        }
        return get().flow().compileList(split).style(template -> itemLineStyle(template.source()));
    }

    /**
     * Renders a template with zMenu's values for a viewer, as legacy text ({@code §} codes), for what only takes text
     * (Bedrock forms, toasts).
     *
     * @param template     the template, compiled once
     * @param placeholders the values, or null
     * @param viewer       the viewer, or null
     * @return the legacy text
     */
    static @NotNull String legacy(@NotNull Template template, @Nullable Placeholders placeholders, @Nullable Pointered viewer) {
        return LegacyComponentSerializer.legacySection().serialize(placeholders == null
                ? template.render(fr.robie.minimessageflow.Placeholder.empty(), viewer)
                : template.render(placeholders.values(), viewer));
    }

    /**
     * zMenu's own placeholders ({@code %zmenu_...%}), asked directly, with or without PlaceholderAPI.
     */
    private static @Nullable String zmenu(@NotNull String key, @Nullable Pointered viewer) {
        if (!key.startsWith(ZMENU_PREFIX) || !(viewer instanceof OfflinePlayer player)) {
            return null;
        }
        String value = LocalPlaceholder.getInstance().onRequest(player, key.substring(ZMENU_PREFIX.length()));
        return value == null ? null : value.replace('\uF000', '%');
    }

    private static @Nullable String player(@NotNull String key, @Nullable Pointered viewer) {
        return key.equals("player") && viewer instanceof OfflinePlayer player ? player.getName() : null;
    }

    /**
     * @return the instance
     */
    @NotNull MiniMessageFlow flow();

    /**
     * Sets the extra MiniMessage tags (Nexo glyphs, a plugin's own), replacing the ones given before.
     *
     * @param tags the tags added to MiniMessage's standard ones
     */
    void tags(@NotNull List<TagResolver> tags);

    /**
     * Applies the configuration read again: the number style, the caches' expiry.
     */
    void configure();
}
