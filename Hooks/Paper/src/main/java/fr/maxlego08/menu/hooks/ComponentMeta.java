package fr.maxlego08.menu.hooks;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.SimpleCache;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ComponentMeta extends MiniMessageColorUtils implements PaperMetaUpdater {
    private final List<TagResolver> tagResolvers = new ArrayList<>();

    private final MenuPlugin plugin;
    private final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);
    private MiniMessage MINI_MESSAGE;
    private final SimpleCache<String, Component> cache = new SimpleCache<>();
    private final Method getLoreMethod;
    private final Method setLoreMethod;
    private final Method nameMethod;
    private final Method inventoryMethod;
    private final Method inventoryTypeMethod;

    public ComponentMeta(MenuPlugin plugin) throws Exception {
        this.plugin = plugin;

        this.getLoreMethod = ItemMeta.class.getDeclaredMethod("lore");
        this.getLoreMethod.setAccessible(true);

        this.setLoreMethod = ItemMeta.class.getDeclaredMethod("lore", List.class);
        this.setLoreMethod.setAccessible(true);
        this.nameMethod = ItemMeta.class.getDeclaredMethod("displayName", Component.class);
        this.nameMethod.setAccessible(true);
        this.inventoryMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, Component.class);
        this.inventoryMethod.setAccessible(true);
        this.inventoryTypeMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, InventoryType.class, Component.class);
        this.inventoryTypeMethod.setAccessible(true);

        this.withStandardTags();
        this.buildMiniMessage();
    }

    @Override
    public void withTagResolver(@NotNull TagResolver tagResolver) {
        Preconditions.checkNotNull(tagResolver, "TagResolver cannot be null");
        this.tagResolvers.add(tagResolver);
    }

    private void withStandardTags() {
        this.withTagResolver(TagResolver.builder().resolver(StandardTags.defaults()).build());
    }

    @Override
    public void buildMiniMessage() {
        MiniMessage.Builder builder = MiniMessage.builder();
        if (!this.tagResolvers.isEmpty()) {
            TagResolver.Builder tagBuilder = TagResolver.builder();
            tagBuilder.resolvers(this.tagResolvers);
            builder.tags(tagBuilder.build());
        }
        this.MINI_MESSAGE = builder.build();
    }

    @Override
    public void clearCache() {
        this.cache.clear();
    }

    private TextDecoration.State getState(String text) {
        return text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>") ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
    }

    @Override
    public @NonNull Component getComponent(String text) {
        return this.cache.get(text, ()->this.MINI_MESSAGE.deserialize(this.colorMiniMessage(text)));
    }

    private void updateDisplayName(ItemMeta itemMeta, String text) {
        Component component = this.cache.get(text, () -> {
            // Fixed text becomes italic automatically
            // From GitHub issue #62
            return this.RESET.append(this.MINI_MESSAGE.deserialize(this.colorMiniMessage(text)).decoration(TextDecoration.ITALIC, this.getState(text)));
        });
        try {
            this.nameMethod.invoke(itemMeta, component);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, Player player) {
        this.updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, OfflinePlayer offlinePlayer) {
        this.updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, Player player) {
        this.updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @Nullable OfflinePlayer offlinePlayer) {
        this.updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @NonNull LoreType loreType) {
        List<Component> components = new ArrayList<>(lore.size());
        for (String text : lore) {
            Component component = this.cache.get(text, () -> {
                // Fixed text becomes italic automatically
                // From GitHub issue #62
                return this.RESET.append(this.MINI_MESSAGE.deserialize(this.colorMiniMessage(text)).decoration(TextDecoration.ITALIC, this.getState(text)));
            });
            components.add(component);
        }

        if (loreType != LoreType.REPLACE && itemMeta.hasLore()) {
            try {
                List<Component> currentLore = (List<Component>) this.getLoreMethod.invoke(itemMeta);

                if (currentLore != null) {
                    if (loreType == LoreType.APPEND) {
                        List<Component> cloneComponents = new ArrayList<>(components);
                        components.clear();
                        components.addAll(currentLore);
                        components.addAll(cloneComponents);
                    } else {
                        components.addAll(currentLore);
                    }
                }

            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        try {
            this.setLoreMethod.invoke(itemMeta, components);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Inventory createInventoryInternal(String inventoryName, InventoryHolder inventoryHolder, Object inventoryTypeOrSize) {
        Component component = this.cache.get(inventoryName, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(inventoryName)));
        try {
            if (inventoryTypeOrSize instanceof Integer integer) {
                return (Inventory) this.inventoryMethod.invoke(null, inventoryHolder, integer, component);
            } else if (inventoryTypeOrSize instanceof InventoryType inventoryType) {
                return (Inventory) this.inventoryTypeMethod.invoke(null, inventoryHolder, inventoryType, component);
            }
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        if (inventoryTypeOrSize instanceof Integer) {
            return Bukkit.createInventory(inventoryHolder, (int) inventoryTypeOrSize, inventoryName);
        } else {
            return Bukkit.createInventory(inventoryHolder, (InventoryType) inventoryTypeOrSize, inventoryName);
        }
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, int size, InventoryHolder inventoryHolder) {
        return this.createInventoryInternal(inventoryName, inventoryHolder, size);
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, @NonNull InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return this.createInventoryInternal(inventoryName, inventoryHolder, inventoryType);
    }

    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull String message) {
        Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(message)));
        sender.sendMessage(component);
    }

    @Override
    public void sendAction(@NonNull Player player, @NonNull String message) {
        Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(message)));
        player.sendActionBar(component);
    }

    @Override
    public void sendTitle(@NonNull Player player, String title, @NonNull String subtitle, long start, long duration, long end) {
        Title.Times times = Title.Times.times(Duration.ofMillis(start), Duration.ofMillis(duration), Duration.ofMillis(end));
        Component componentTitle = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(title)));
        Component componentSubTitle = this.cache.get(subtitle, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(subtitle)));
        player.showTitle(Title.title(componentTitle, componentSubTitle, times));
    }

    @Override
    public void openBook(@NonNull Player player, @NonNull String title, @NonNull String author, @NonNull List<String> lines) {

        Component titleComponent = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(title)));
        Component authorComponent = this.cache.get(author, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(author)));
        List<Component> linesComponent = new ArrayList<>(lines.size());
        for (String text : lines) {
            String result = this.plugin.parse(player, text);
            Component component = this.cache.get(result, () -> this.MINI_MESSAGE.deserialize(this.colorMiniMessage(result)));
            linesComponent.add(component);
        }

        Book book = Book.book(titleComponent, authorComponent, linesComponent);
        player.openBook(book);
    }

    @Override
    public String getLegacyMessage(String message) {
        return message == null ? null : LegacyComponentSerializer.legacySection().serialize(this.getComponent(message));
    }

    public String getMiniMessage(Component component) {
        return component == null ? null : this.MINI_MESSAGE.serialize(component);
    }
}
