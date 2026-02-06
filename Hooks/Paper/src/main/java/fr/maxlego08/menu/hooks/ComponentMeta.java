package fr.maxlego08.menu.hooks;

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
import org.jetbrains.annotations.Nullable;
import org.jspecify.annotations.NonNull;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

public class ComponentMeta extends MiniMessageColorUtils implements PaperMetaUpdater {
    private final MenuPlugin plugin;
    private final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);
    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
    private final SimpleCache<String, Component> cache = new SimpleCache<>();
    private final Method getLoreMethod;
    private final Method setLoreMethod;
    private final Method nameMethod;
    private final Method inventoryMethod;
    private final Method inventoryTypeMethod;

    public ComponentMeta(MenuPlugin plugin) throws Exception {
        this.plugin = plugin;

        getLoreMethod = ItemMeta.class.getDeclaredMethod("lore");
        getLoreMethod.setAccessible(true);

        setLoreMethod = ItemMeta.class.getDeclaredMethod("lore", List.class);
        setLoreMethod.setAccessible(true);
        nameMethod = ItemMeta.class.getDeclaredMethod("displayName", Component.class);
        nameMethod.setAccessible(true);
        inventoryMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, Component.class);
        inventoryMethod.setAccessible(true);
        inventoryTypeMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, InventoryType.class, Component.class);
        inventoryTypeMethod.setAccessible(true);
    }

    private TextDecoration.State getState(String text) {
        return text.contains("&o") || text.contains("<i>") || text.contains("<em>") || text.contains("<italic>") ? TextDecoration.State.TRUE : TextDecoration.State.FALSE;
    }

    @Override
    public @NonNull Component getComponent(String text) {
        return this.cache.get(text, ()->this.MINI_MESSAGE.deserialize(colorMiniMessage(text)));
    }

    private void updateDisplayName(ItemMeta itemMeta, String text) {
        Component component = this.cache.get(text, () -> {
            // Fixed text becomes italic automatically
            // From GitHub issue #62
            return RESET.append(this.MINI_MESSAGE.deserialize(colorMiniMessage(text)).decoration(TextDecoration.ITALIC, getState(text)));
        });
        try {
            nameMethod.invoke(itemMeta, component);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, Player player) {
        updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateDisplayName(@NonNull ItemMeta itemMeta, String text, OfflinePlayer offlinePlayer) {
        updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, Player player) {
        updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @Nullable OfflinePlayer offlinePlayer) {
        updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void updateLore(@NonNull ItemMeta itemMeta, @NonNull List<String> lore, @NonNull LoreType loreType) {
        List<Component> components = new ArrayList<>(lore.size());
        for (String text : lore) {
            Component component = this.cache.get(text, () -> {
                // Fixed text becomes italic automatically
                // From GitHub issue #62
                return RESET.append(this.MINI_MESSAGE.deserialize(colorMiniMessage(text)).decoration(TextDecoration.ITALIC, getState(text)));
            });
            components.add(component);
        }

        if (loreType != LoreType.REPLACE && itemMeta.hasLore()) {
            try {
                List<Component> currentLore = (List<Component>) getLoreMethod.invoke(itemMeta);

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
            setLoreMethod.invoke(itemMeta, components);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    private Inventory createInventoryInternal(String inventoryName, InventoryHolder inventoryHolder, Object inventoryTypeOrSize) {
        Component component = this.cache.get(inventoryName, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(inventoryName)));
        try {
            if (inventoryTypeOrSize instanceof Integer integer) {
                return (Inventory) inventoryMethod.invoke(null, inventoryHolder, integer, component);
            } else if (inventoryTypeOrSize instanceof InventoryType inventoryType) {
                return (Inventory) inventoryTypeMethod.invoke(null, inventoryHolder, inventoryType, component);
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
        return createInventoryInternal(inventoryName, inventoryHolder, size);
    }

    @Override
    public @NonNull Inventory createInventory(@NonNull String inventoryName, @NonNull InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return createInventoryInternal(inventoryName, inventoryHolder, inventoryType);
    }




    @Override
    public void sendMessage(@NonNull CommandSender sender, @NonNull String message) {
        Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
        sender.sendMessage(component);
    }

    @Override
    public void sendAction(@NonNull Player player, @NonNull String message) {
        Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
        player.sendActionBar(component);
    }

    @Override
    public void sendTitle(@NonNull Player player, String title, @NonNull String subtitle, long start, long duration, long end) {
        Title.Times times = Title.Times.times(Duration.ofMillis(start), Duration.ofMillis(duration), Duration.ofMillis(end));
        Component componentTitle = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(title)));
        Component componentSubTitle = this.cache.get(subtitle, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(subtitle)));
        player.showTitle(Title.title(componentTitle, componentSubTitle, times));
    }

    @Override
    public void openBook(@NonNull Player player, @NonNull String title, @NonNull String author, @NonNull List<String> lines) {

        Component titleComponent = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(title)));
        Component authorComponent = this.cache.get(author, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(author)));
        List<Component> linesComponent = new ArrayList<>(lines.size());
        for (String text : lines) {
            String result = plugin.parse(player, text);
            Component component = this.cache.get(result, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(result)));
            linesComponent.add(component);
        }

        Book book = Book.book(titleComponent, authorComponent, linesComponent);
        player.openBook(book);
    }

    @Override
    public String getLegacyMessage(String message) {
        return message == null ? null : LegacyComponentSerializer.legacySection().serialize(this.getComponent(message));
    }
}
