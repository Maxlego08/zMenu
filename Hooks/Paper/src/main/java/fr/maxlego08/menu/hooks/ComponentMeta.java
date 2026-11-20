package fr.maxlego08.menu.hooks;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

/**
 * Formats zMenu's text with MiniMessageFlow ({@link TextService}).
 *
 * <p>The methods taking a {@code String} format text whose values may already be in it: MiniMessageFlow keeps what it
 * compiled in a bounded cache. Configured text should be compiled once and rendered with {@link #render} or a
 * template. MiniMessage is only used to turn a component back into text ({@link #getMiniMessage}).</p>
 */
public class ComponentMeta implements PaperMetaUpdater {

    private final List<TagResolver> tagResolvers = new ArrayList<>();
    private MiniMessage miniMessage = MiniMessage.miniMessage();

    public ComponentMeta(MenuPlugin plugin) {
        this.buildMiniMessage();
        TextService.get().configure();
    }

    private @NotNull Component format(@NotNull String text) {
        return TextService.get().flow().format(text);
    }

    /**
     * An item's name or lore line: not italic, unless the text asks for italic itself (as zMenu always did, the
     * client showing item text in italic by default).
     */
    private @NotNull Component formatItemLine(@NotNull String text) {
        return this.format(text).decoration(TextDecoration.ITALIC, italicState(text));
    }

    /**
     * The italic state zMenu gives a line of item text: true when the text asks for italic itself.
     *
     * @param text the line as written
     * @return its italic state
     */
    public static @NotNull TextDecoration.State italicState(@NotNull String text) {
        return text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>")
                ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
    }

    @Override
    public void withTagResolver(@NotNull TagResolver tagResolver) {
        Preconditions.checkNotNull(tagResolver, "TagResolver cannot be null");
        this.tagResolvers.add(tagResolver);
    }

    /**
     * Gives the tags added through {@link #withTagResolver} to MiniMessageFlow, and to the MiniMessage instance that
     * turns components back into text.
     */
    @Override
    public void buildMiniMessage() {
        MiniMessage.Builder builder = MiniMessage.builder();
        if (!this.tagResolvers.isEmpty()) {
            builder.editTags(tags -> tags.resolvers(this.tagResolvers));
        }
        this.miniMessage = builder.build();
        TextService.get().tags(List.copyOf(this.tagResolvers));
    }

    @Override
    public void clearCache() {
        TextService.get().configure();
    }

    @Override
    public @NonNull Component getComponent(String text) {
        return text == null ? Component.empty() : this.format(text);
    }

    @Override
    public @NotNull TextService getTextService() {
        return TextService.get();
    }

    @Override
    public @NotNull Component render(@NotNull String text, @Nullable Placeholders placeholders, @Nullable Player player) {
        return placeholders == null ? TextService.get().flow().format(text, fr.robie.minimessageflow.Placeholder.empty(), player)
                : TextService.get().flow().format(text, placeholders.values(), player);
    }

    @Override
    public void updateDisplayName(@NotNull ItemMeta itemMeta, @NotNull Component name) {
        itemMeta.displayName(name);
    }

    @Override
    public void updateLoreComponents(@NotNull ItemMeta itemMeta, @NotNull List<Component> lore, @NotNull LoreType loreType) {
        List<Component> components = lore;
        if (loreType != LoreType.REPLACE && itemMeta.hasLore()) {
            List<Component> currentLore = itemMeta.lore();
            if (currentLore != null) {
                components = new ArrayList<>(lore.size() + currentLore.size());
                if (loreType == LoreType.APPEND) {
                    components.addAll(currentLore);
                    components.addAll(lore);
                } else {
                    components.addAll(lore);
                    components.addAll(currentLore);
                }
            }
        }
        itemMeta.lore(components);
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, Player player) {
        if (text != null) {
            this.updateDisplayName(itemMeta, this.formatItemLine(text));
        }
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, OfflinePlayer offlinePlayer) {
        if (text != null) {
            this.updateDisplayName(itemMeta, this.formatItemLine(text));
        }
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, Player player) {
        this.updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @Nullable OfflinePlayer offlinePlayer) {
        this.updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @NonNull LoreType loreType) {
        List<Component> components = new ArrayList<>(lore.size());
        for (String text : lore) {
            components.add(this.formatItemLine(text));
        }
        this.updateLoreComponents(itemMeta, components, loreType);
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, int size, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, size, this.format(inventoryName));
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, @NonNull InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return Bukkit.createInventory(inventoryHolder, inventoryType, this.format(inventoryName));
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull String message) {
        sender.sendMessage(this.format(message));
    }

    @Override
    public void sendAction(@NonNull Player player, @NonNull String message) {
        player.sendActionBar(this.format(message));
    }

    @Override
    public void sendTitle(@NonNull Player player, String title, @NonNull String subtitle, long start, long duration, long end) {
        Title.Times times = Title.Times.times(Duration.ofMillis(start), Duration.ofMillis(duration), Duration.ofMillis(end));
        player.showTitle(Title.title(this.getComponent(title), this.format(subtitle), times));
    }

    @Override
    public void openBook(@NonNull Player player, @NonNull String title, @NonNull String author, @NonNull List<String> lines) {
        List<Component> pages = new ArrayList<>(lines.size());
        for (String text : lines) {
            pages.add(this.format(text));
        }
        player.openBook(Book.book(this.format(title), this.format(author), pages));
    }

    @Override
    public String getLegacyMessage(String message) {
        return message == null ? null : LegacyComponentSerializer.legacySection().serialize(this.format(message));
    }

    @Override
    public String getMiniMessage(Component component) {
        return component == null ? null : this.miniMessage.serialize(component);
    }
}
