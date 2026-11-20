package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.text.TextService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PaperMetaUpdater extends MetaUpdater {
    void withTagResolver(@NotNull TagResolver tagResolver);

    void buildMiniMessage();

    void clearCache();

    @NotNull Component getComponent(@Nullable String text);

    /**
     * @return the text service zMenu formats with
     */
    default @NotNull TextService getTextService() {
        return TextService.get();
    }

    /**
     * Renders a configured text for a player: compiled once (cached by its text), rendered with zMenu's placeholders,
     * then PlaceholderAPI's. Use it instead of {@code getComponent(plugin.parse(player, placeholders.parse(text)))}.
     *
     * @param text         the text as configured, its placeholders not resolved
     * @param placeholders zMenu's placeholders, or null for PlaceholderAPI only
     * @param player       the player PlaceholderAPI is asked for and who sees the text, or null for neither
     * @return the component
     */
    @NotNull Component render(@NotNull String text, @Nullable Placeholders placeholders, @Nullable Player player);

    /**
     * Sets an item's display name, already formatted.
     */
    void updateDisplayName(@NotNull ItemMeta itemMeta, @NotNull Component name);

    /**
     * Sets an item's lore, already formatted.
     */
    void updateLoreComponents(@NotNull ItemMeta itemMeta, @NotNull List<Component> lore, @NotNull LoreType loreType);

    default String getMiniMessage(Component component) {
        return component == null ? null : MiniMessage.miniMessage().serialize(component);
    }
}
