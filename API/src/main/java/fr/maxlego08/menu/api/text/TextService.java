package fr.maxlego08.menu.api.text;

import fr.robie.minimessageflow.FailedModifier;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.ValueKeys;
import net.kyori.adventure.pointer.Pointered;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
     * placeholders read inside formatted values, {@code %player%} as the viewer's name.
     *
     * @param builder the builder of an instance
     */
    static void options(@NotNull MiniMessageFlow.Builder builder) {
        builder.keys(KEYS)
                .legacyPrefixModifiers(true)
                .failedModifier(FailedModifier.EMPTY)
                .placeholdersInFormattedValues(true)
                .resolver(TextService::player);
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
