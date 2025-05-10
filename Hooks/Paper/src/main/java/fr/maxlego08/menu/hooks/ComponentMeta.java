package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.SimpleCache;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ComponentMeta implements MetaUpdater {

    private final MenuPlugin plugin;
    private final Component RESET = Component.empty().decoration(TextDecoration.ITALIC, false);
    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
    private final Map<String, String> COLORS_MAPPINGS = new HashMap<>();
    private final SimpleCache<String, Component> cache = new SimpleCache<>();
    private final Method getLoreMethod;
    private final Method setLoreMethod;
    private final Method nameMethod;
    private final Method inventoryMethod;
    private final Method inventoryTypeMethod;

    public ComponentMeta(MenuPlugin plugin) throws Exception {
        this.plugin = plugin;
        this.COLORS_MAPPINGS.put("0", "black");
        this.COLORS_MAPPINGS.put("1", "dark_blue");
        this.COLORS_MAPPINGS.put("2", "dark_green");
        this.COLORS_MAPPINGS.put("3", "dark_aqua");
        this.COLORS_MAPPINGS.put("4", "dark_red");
        this.COLORS_MAPPINGS.put("5", "dark_purple");
        this.COLORS_MAPPINGS.put("6", "gold");
        this.COLORS_MAPPINGS.put("7", "gray");
        this.COLORS_MAPPINGS.put("8", "dark_gray");
        this.COLORS_MAPPINGS.put("9", "blue");
        this.COLORS_MAPPINGS.put("a", "green");
        this.COLORS_MAPPINGS.put("b", "aqua");
        this.COLORS_MAPPINGS.put("c", "red");
        this.COLORS_MAPPINGS.put("d", "light_purple");
        this.COLORS_MAPPINGS.put("e", "yellow");
        this.COLORS_MAPPINGS.put("f", "white");
        this.COLORS_MAPPINGS.put("k", "obfuscated");
        this.COLORS_MAPPINGS.put("l", "bold");
        this.COLORS_MAPPINGS.put("m", "strikethrough");
        this.COLORS_MAPPINGS.put("n", "underlined");
        this.COLORS_MAPPINGS.put("o", "italic");
        this.COLORS_MAPPINGS.put("r", "reset");

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

    public Component getComponent(String text) {
        return this.MINI_MESSAGE.deserialize(colorMiniMessage(text));
    }

    private void updateDisplayName(ItemMeta itemMeta, String text) {
        Component component = this.cache.get(text, () -> {
            //Fixed text becomes italic automatically
            //From GitHub issue #62
            return RESET.append(this.MINI_MESSAGE.deserialize(colorMiniMessage(text)).decoration(TextDecoration.ITALIC, getState(text)));
        });
        try {
            nameMethod.invoke(itemMeta, component);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
        updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, OfflinePlayer offlinePlayer) {
        updateDisplayName(itemMeta, text);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
        updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, OfflinePlayer offlinePlayer) {
        updateLore(itemMeta, lore, LoreType.REPLACE);
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, LoreType loreType) {
        List<Component> components = lore.stream().map(text -> this.cache.get(text, () -> {
            //Fixed text becomes italic automatically
            //From GitHub issue #62
            return RESET.append(this.MINI_MESSAGE.deserialize(colorMiniMessage(text)).decoration(TextDecoration.ITALIC, getState(text)));
        })).collect(Collectors.toList());

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
            if (inventoryTypeOrSize instanceof Integer) {
                return (Inventory) inventoryMethod.invoke(null, inventoryHolder, inventoryTypeOrSize, component);
            } else if (inventoryTypeOrSize instanceof InventoryType) {
                return (Inventory) inventoryTypeMethod.invoke(null, inventoryHolder, inventoryTypeOrSize, component);
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
    public Inventory createInventory(String inventoryName, int size, InventoryHolder inventoryHolder) {
        return createInventoryInternal(inventoryName, inventoryHolder, size);
    }

    @Override
    public Inventory createInventory(String inventoryName, InventoryType inventoryType, InventoryHolder inventoryHolder) {
        return createInventoryInternal(inventoryName, inventoryHolder, inventoryType);
    }


    private String colorMiniMessage(String message) {

        String newMessage = message;

        Pattern pattern = Pattern.compile("(?<!<)(?<!:)#[a-fA-F0-9]{6}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            newMessage = newMessage.replace(color, "<" + color + ">");
            message = message.replace(color, "");
            matcher = pattern.matcher(message);
        }

        for (Entry<String, String> entry : this.COLORS_MAPPINGS.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            newMessage = newMessage.replace("&" + key, "<" + value + ">");
            newMessage = newMessage.replace("§" + key, "<" + value + ">");
            newMessage = newMessage.replace("&" + key.toUpperCase(), "<" + value + ">");
            newMessage = newMessage.replace("§" + key.toUpperCase(), "<" + value + ">");
        }

        return newMessage;
    }

    @Override
    public void sendMessage(CommandSender sender, String message) {
        if (sender instanceof Audience) {
            Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
            ((Audience) sender).sendMessage(component);
        }
    }

    @Override
    public void sendAction(Player player, String message) {
        if (player instanceof Audience) {
            Component component = this.cache.get(message, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(message)));
            ((Audience) player).sendActionBar(component);
        }
    }

    @Override
    public void sendTitle(Player player, String title, String subtitle, long start, long duration, long end) {
        if (player instanceof Audience) {
            Title.Times times = Title.Times.times(Duration.ofMillis(start), Duration.ofMillis(duration), Duration.ofMillis(end));
            Component componentTitle = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(title)));
            Component componentSubTitle = this.cache.get(subtitle, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(subtitle)));
            ((Audience) player).showTitle(Title.title(componentTitle, componentSubTitle, times));
        }
    }

    @Override
    public void openBook(Player player, String title, String author, List<String> lines) {

        Component titleComponent = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(title)));
        Component authorComponent = this.cache.get(author, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(author)));
        List<Component> linesComponent = lines.stream().map(text -> {
            String result = plugin.parse(player, text);
            return this.cache.get(result, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(result)));
        }).collect(Collectors.toList());

        Book book = Book.book(titleComponent, authorComponent, linesComponent);
        if (player instanceof Audience) {
            ((Audience) player).openBook(book);
        }
    }
}
