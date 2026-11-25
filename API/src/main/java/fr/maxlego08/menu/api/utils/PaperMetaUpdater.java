package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.ValueTag;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface PaperMetaUpdater extends MetaUpdater {
    /**
     * Adds MiniMessage tags whose output is always the same (a font's glyphs): they are read once, when a text is
     * compiled. For a tag whose output changes with the viewer or with time, use {@link #withValueTag}.
     *
     * @param tagResolver the tags
     */
    void withTagResolver(@NotNull TagResolver tagResolver);

    /**
     * Adds a tag computed at each render, for the viewer ({@code <name:args>}, also {@code %name:args%}): a value
     * that changes with the player or with time. A text using it is never kept as a built item.
     *
     * @param name the tag's name
     * @param tag  computes the value
     */
    default void withValueTag(@NotNull String name, @NotNull ValueTag tag) {
        this.getTextService().flow().valueTag(name, tag);
    }

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

    /**
     * Creates a chest inventory with a formatted title.
     *
     * @param title  the title
     * @param size   the size, a multiple of 9
     * @param holder the holder
     * @return the inventory
     */
    default @NotNull Inventory createInventory(@NotNull Component title, int size, @Nullable InventoryHolder holder) {
        return Bukkit.createInventory(holder, size, title);
    }

    /**
     * Creates an inventory of a type with a formatted title.
     *
     * @param title  the title
     * @param type   the type
     * @param holder the holder
     * @return the inventory
     */
    default @NotNull Inventory createInventory(@NotNull Component title, @NotNull InventoryType type, @Nullable InventoryHolder holder) {
        return Bukkit.createInventory(holder, type, title);
    }

    default String getMiniMessage(Component component) {
        return component == null ? null : MiniMessage.miniMessage().serialize(component);
    }
}
