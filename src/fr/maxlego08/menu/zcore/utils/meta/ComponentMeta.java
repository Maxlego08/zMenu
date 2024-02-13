package fr.maxlego08.menu.zcore.utils.meta;

import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.zcore.utils.SimpleCache;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ComponentMeta extends ZUtils implements MetaUpdater {

    private final MiniMessage MINI_MESSAGE = MiniMessage.builder().tags(TagResolver.builder().resolver(StandardTags.defaults()).build()).build();
    private final Map<String, String> COLORS_MAPPINGS = new HashMap<>();
    private final SimpleCache<String, Component> cache = new SimpleCache<>();
    private final Method loreMethod;
    private final Method nameMethod;
    private final Method inventoryMethod;

    public ComponentMeta() throws Exception {
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

        loreMethod = ItemMeta.class.getDeclaredMethod("lore", List.class);
        loreMethod.setAccessible(true);
        nameMethod = ItemMeta.class.getDeclaredMethod("displayName", Component.class);
        nameMethod.setAccessible(true);
        inventoryMethod = Bukkit.class.getMethod("createInventory", InventoryHolder.class, int.class, Component.class);
        inventoryMethod.setAccessible(true);
    }

    @Override
    public void updateDisplayName(ItemMeta itemMeta, String text, Player player) {
        String result = papi(text, player);
        Component component = this.cache.get(result, () -> {
            return this.MINI_MESSAGE.deserialize(colorMiniMessage(result)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE); // We will force the italics in false, otherwise it will activate for no reason
        });
        try {
            nameMethod.invoke(itemMeta, component);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public void updateLore(ItemMeta itemMeta, List<String> lore, Player player) {
        List<Component> components = lore.stream().map(text -> {
            String result = papi(text, player);
            return this.cache.get(result, () -> {
                return this.MINI_MESSAGE.deserialize(colorMiniMessage(result)).decoration(TextDecoration.ITALIC, TextDecoration.State.FALSE); // We will force the italics in false, otherwise it will activate for no reason
            });
        }).collect(Collectors.toList());

        try {
            loreMethod.invoke(itemMeta, components);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    @Override
    public Inventory createInventory(String inventoryName, int size, InventoryHolder inventoryHolder) {
        Component component = this.cache.get(inventoryName, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(inventoryName)));
        try {
            return (Inventory) inventoryMethod.invoke(null, inventoryHolder, size, component);
        } catch (IllegalAccessException | InvocationTargetException exception) {
            exception.printStackTrace();
        }
        return Bukkit.createInventory(inventoryHolder, size, color(inventoryName));
    }

    private String colorMiniMessage(String message) {

        String newMessage = message;

        if (NMSUtils.isHexColor()) {
            Pattern pattern = Pattern.compile("(?<!<)(?<!:)#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                newMessage = newMessage.replace(color, "<" + color + ">");
                message = message.replace(color, "");
                matcher = pattern.matcher(message);
            }
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
    public void sendTitle(Player player, String title, String subtitle, long start, long duration, long end) {
        if (player instanceof Audience) {
            Title.Times times = Title.Times.times(Duration.ofMillis(500), Duration.ofMillis(3000), Duration.ofMillis(1000));
            Component componentTitle = this.cache.get(title, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(title)));
            Component componentSubTitle = this.cache.get(subtitle, () -> this.MINI_MESSAGE.deserialize(colorMiniMessage(subtitle)));
            ((Audience) player).showTitle(Title.title(componentTitle, componentSubTitle, times));
        }
    }
}
