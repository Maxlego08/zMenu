package fr.maxlego08.menu.zcore.utils;

import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.scheduler.ZScheduler;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.builder.CooldownBuilder;
import fr.maxlego08.menu.zcore.utils.builder.TimerBuilder;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public abstract class ZUtils extends MessageUtils {

    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time
    private static final SimpleCache<String, Object> cache = new SimpleCache<>();
    // For plugin support from 1.8 to 1.12
    private static Material[] byId;

    static {
        if (!NmsVersion.nmsVersion.isNewMaterial()) {
            byId = new Material[0];
            for (Material material : Material.values()) {
                if (byId.length <= material.getId()) {
                    byId = Arrays.copyOfRange(byId, 0, material.getId() + 2);
                }
                byId[material.getId()] = material;
            }
        }
    }

    protected String findPlayerLocale(Player player) {
        if (NmsVersion.getCurrentVersion().getVersion() >= NmsVersion.V_1_13.getVersion()) {
            try {
                return player != null ? player.getLocale() : null;
            } catch (Exception exception) {
                return null;
            }
        }
        return null;
    }

    protected boolean isMinecraftName(String username) {
        String MINECRAFT_USERNAME_REGEX = "^[a-zA-Z0-9_]{3,16}$";
        Pattern pattern = Pattern.compile(MINECRAFT_USERNAME_REGEX);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }

    protected int parseInt(String value, int defaultValue) {
        try {
            return Integer.parseInt(value);
        } catch (Exception exception) {
            return defaultValue;
        }
    }

    protected int getInventorySizeByMatrix(List<String> matrix) {
        return Math.min(matrix.size() * 9, 54);
    }

    protected Map<Character, List<Integer>> generateMatrix(List<String> matrix) {
        Map<Character, List<Integer>> charMap = new HashMap<>();

        if (matrix == null || matrix.size() > 6) {
            Logger.info("Matrix is wrong !", Logger.LogType.ERROR);
            return new HashMap<>();
        }
        for (String line : matrix) {
            if (line.length() > 9) {
                Logger.info("Each line of the matrix must have exactly 9 characters.", Logger.LogType.ERROR);
                return new HashMap<>();
            }
        }

        for (int i = 0; i < matrix.size(); i++) {
            for (int j = 0; j < Math.min(matrix.get(i).length(), 9); j++) {
                char c = matrix.get(i).charAt(j);
                if (c != ' ') {
                    int slot = i * 9 + j;
                    charMap.computeIfAbsent(c, k -> new ArrayList<>()).add(slot);
                }
            }
        }

        return charMap;
    }

    /**
     * Allows to encode an itemStack in base64
     *
     * @param item - ItemStack
     * @return the encoded item
     */
    protected String encode(ItemStack item) {
        return ItemStackUtils.serializeItemStack(item);
    }

    /**
     * Allows to decode a string in ItemStack
     *
     * @param item - the encoded itemStack
     * @return the decoded item
     */
    protected ItemStack decode(String item) {
        return ItemStackUtils.deserializeItemStack(item);
    }

    /**
     * Allows to obtain a random number between a and b
     *
     * @param first  First number
     * @param second second number
     * @return number between a and b
     */
    protected int getNumberBetween(int first, int second) {
        return ThreadLocalRandom.current().nextInt(first, second);
    }

    /**
     * Allows you to check if the inventory is full
     *
     * @param player Target player
     * @return true if the player's inventory is full
     */
    protected boolean hasInventoryFull(Player player) {
        int slot = 0;
        PlayerInventory inventory = player.getInventory();
        for (int a = 0; a != 36; a++) {
            ItemStack itemStack = inventory.getContents()[a];
            if (itemStack == null) slot++;
        }
        return slot == 0;
    }

    /**
     * Gives an item to the player, if the player's inventory is full then the
     * item will drop to the ground
     *
     * @param player
     * @param item
     */
    protected void give(Player player, ItemStack item) {
        if (hasInventoryFull(player)) player.getWorld().dropItem(player.getLocation(), item);
        else player.getInventory().addItem(item);
    }

    /**
     * Allows to return a material according to its ID Works only for plugins
     * from 1.8 to 1.12
     *
     * @param id
     * @return the material according to his id
     */
    protected Material getMaterial(int id) {
        return byId.length > id && id >= 0 ? byId[id] : Material.AIR;
    }

    /**
     * Allows to check if an itemstack has a display name
     *
     * @return boolean
     */
    protected boolean hasDisplayName(ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName();
    }

    /**
     * Check if the item name is the same as the given string
     *
     * @param itemStack
     * @param name
     * @return true if the item name is the same as string
     */
    protected boolean same(ItemStack itemStack, String name) {
        return this.hasDisplayName(itemStack) && itemStack.getItemMeta().getDisplayName().equals(name);
    }

    /**
     * Check if the item name contains the given string
     *
     * @param itemStack
     * @param name
     * @return true if the item name contains the string
     */
    protected boolean contains(ItemStack itemStack, String name) {
        return this.hasDisplayName(itemStack) && itemStack.getItemMeta().getDisplayName().contains(name);
    }

    /**
     * Removes a specified amount of items from the player's hand.
     *
     * @param player the Player whose item in hand is to be removed
     */
    protected void removeItemInHand(Player player) {
        removeItemInHand(player, 64);
    }

    /**
     * Removes a specified amount of items from the player's hand.
     *
     * @param player the Player whose item in hand is to be removed
     * @param how    the number of items to remove
     */
    protected void removeItemInHand(Player player, int how) {
        if (player.getItemInHand().getAmount() > how)
            player.getItemInHand().setAmount(player.getItemInHand().getAmount() - how);
        else player.setItemInHand(new ItemStack(Material.AIR));
        player.updateInventory();
    }

    /**
     * Checks if two locations are the same, comparing their block coordinates and world names.
     *
     * @param l  the first Location to compare
     * @param l2 the second Location to compare
     * @return true if the locations are the same, false otherwise
     */
    protected boolean same(Location l, Location l2) {
        return (l.getBlockX() == l2.getBlockX()) && (l.getBlockY() == l2.getBlockY()) && (l.getBlockZ() == l2.getBlockZ()) && l.getWorld().getName().equals(l2.getWorld().getName());
    }

    /**
     * Formats a decimal number to two decimal places.
     *
     * @param decimal the number to format
     * @return the formatted number as a String
     */
    protected String format(double decimal) {
        return format(decimal, "#.##");
    }

    /**
     * Formats a decimal number according to the specified format.
     *
     * @param decimal the number to format
     * @param format  the format pattern to apply
     * @return the formatted number as a String
     */
    protected String format(double decimal, String format) {
        DecimalFormat decimalFormat = new DecimalFormat(format);
        return decimalFormat.format(decimal);
    }


    /**
     * Remove a certain number of items from a player's inventory
     *
     * @param player    - Player who will have items removed
     * @param amount    - Number of items to remove
     * @param itemStack - ItemStack to be removed
     */
    protected void removeItems(Player player, int amount, ItemStack itemStack) {
        int slot = 0;
        for (ItemStack is : player.getInventory().getContents()) {
            if (is != null && is.isSimilar(itemStack) && amount > 0) {
                int currentAmount = is.getAmount() - amount;
                amount -= is.getAmount();
                if (currentAmount <= 0) {
                    if (slot == 40) {
                        player.getInventory().setItemInOffHand(null);
                    } else {
                        player.getInventory().removeItem(is);
                    }
                } else {
                    is.setAmount(currentAmount);
                }
            }
            slot++;
        }
        player.updateInventory();
    }

    protected void schedule(long delay, Runnable runnable) {
        ZScheduler.TIMER.schedule(new TimerTask() {

            @Override
            public void run() {
                if (runnable != null) runnable.run();
            }
        }, delay);
    }

    /**
     * Converts a string to a properly formatted name by replacing underscores with spaces and capitalizing the first letter.
     *
     * @param string the input string
     * @return the formatted name
     */
    protected String name(String string) {
        String name = string.replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Converts a Material enum name to a properly formatted name by replacing underscores with spaces and capitalizing the first letter.
     *
     * @param string the Material enum
     * @return the formatted name
     */
    protected String name(Material string) {
        String name = string.name().replace("_", " ").toLowerCase();
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    /**
     * Retrieves the name of an item from the given ItemStack.
     *
     * @param itemStack the ItemStack containing the item
     * @return the name of the item
     */
    protected String name(ItemStack itemStack) {
        return this.getItemName(itemStack);
    }

    /**
     * Calculates the maximum number of pages needed to display a collection of items with each page containing up to 45 items.
     *
     * @param items the collection of items
     * @return the maximum number of pages
     */
    protected int getMaxPage(Collection<?> items) {
        return (items.size() / 45) + 1;
    }

    /**
     * Calculates the maximum number of pages needed to display a collection of items with each page containing up to the specified number of items.
     *
     * @param items the collection of items
     * @param a     the number of items per page
     * @return the maximum number of pages
     */
    protected int getMaxPage(Collection<?> items, int a) {
        return (items.size() / a) + 1;
    }


    /**
     * Calculates the percentage of a value relative to a total.
     *
     * @param value the part value
     * @param total the total value
     * @return the percentage of the value relative to the total
     */
    protected double percent(double value, double total) {
        return (value * 100) / total;
    }

    /**
     * Calculates the numerical value of a given percentage of a total.
     *
     * @param total   the total value
     * @param percent the percentage to be calculated
     * @return the numerical value of the percentage of the total
     */
    protected double percentNum(double total, double percent) {
        return total * (percent / 100);
    }

    /**
     * Schedules a repetitive task to run at a fixed rate for a specific count.
     *
     * @param plugin   the Plugin for which the task is scheduled
     * @param delay    the delay in milliseconds between successive task executions
     * @param count    the number of times the task should be executed
     * @param runnable the Runnable task to be executed
     */
    protected void schedule(Plugin plugin, long delay, int count, Runnable runnable) {
        ZScheduler.TIMER.scheduleAtFixedRate(new TimerTask() {
            int tmpCount = 0;

            @Override
            public void run() {

                if (!plugin.isEnabled()) {
                    this.cancel();
                    return;
                }

                if (tmpCount > count) {
                    this.cancel();
                    return;
                }

                tmpCount++;
                Bukkit.getScheduler().runTask(plugin, runnable);

            }
        }, 0, delay);
    }

    /**
     * Creates an inventory for the specified plugin and player with the given inventory type.
     *
     * @param plugin    the MenuPlugin for which the inventory is created
     * @param player    the Player for whom the inventory is created
     * @param inventory the type of EnumInventory to be created
     */
    protected void createInventory(MenuPlugin plugin, Player player, EnumInventory inventory) {
        createInventory(plugin, player, inventory, 1);
    }

    /**
     * Creates an inventory for the specified plugin and player with the given inventory type and page number.
     *
     * @param plugin    the MenuPlugin for which the inventory is created
     * @param player    the Player for whom the inventory is created
     * @param inventory the type of EnumInventory to be created
     * @param page      the page number of the inventory
     */
    protected void createInventory(MenuPlugin plugin, Player player, EnumInventory inventory, int page) {
        createInventory(plugin, player, inventory, page, new Object() {
        });
    }

    /**
     * Creates an inventory for the specified plugin and player with the given inventory type, page number, and additional objects.
     *
     * @param plugin    the MenuPlugin for which the inventory is created
     * @param player    the Player for whom the inventory is created
     * @param inventory the type of EnumInventory to be created
     * @param page      the page number of the inventory
     * @param objects   additional objects to be passed for inventory creation
     */
    protected void createInventory(MenuPlugin plugin, Player player, EnumInventory inventory, int page, Object... objects) {
        plugin.getVInventoryManager().createInventory(inventory, player, page, objects);
    }

    /**
     * Creates an inventory for the specified plugin and player with the given inventory ID, page number, and additional objects.
     *
     * @param plugin    the MenuPlugin for which the inventory is created
     * @param player    the Player for whom the inventory is created
     * @param inventory the ID of the inventory to be created
     * @param page      the page number of the inventory
     * @param objects   additional objects to be passed for inventory creation
     */
    protected void createInventory(MenuPlugin plugin, Player player, int inventory, int page, Object... objects) {
        plugin.getVInventoryManager().createInventory(inventory, player, page, objects);
    }

    /**
     * Checks if the given permissible has the specified permission.
     *
     * @param permissible the Permissible to check
     * @param permission  the Permission to check for
     * @return true if the permissible has the permission, false otherwise
     */
    protected boolean hasPermission(Permissible permissible, Permission permission) {
        return permissible.hasPermission(permission.getPermission());
    }

    /**
     * Checks if the given permissible has the specified permission.
     *
     * @param permissible the Permissible to check
     * @param permission  the permission string to check for
     * @return true if the permissible has the permission, false otherwise
     */
    protected boolean hasPermission(Permissible permissible, String permission) {
        return permissible.hasPermission(permission);
    }


    /**
     * Schedules a fixed-rate execution of a task associated with the given plugin.
     *
     * @param plugin   the MenuPlugin for which the task is scheduled
     * @param delay    the delay in milliseconds before task is to be executed and between successive task executions
     * @param consumer the BiConsumer to be executed with the TimerTask and a boolean indicating the task's success
     * @return the scheduled TimerTask
     */
    protected TimerTask scheduleFix(MenuPlugin plugin, long delay, BiConsumer<TimerTask, Boolean> consumer) {
        return this.scheduleFix(plugin, delay, delay, consumer);
    }

    /**
     * Schedules a fixed-rate execution of a task associated with the given plugin.
     *
     * @param plugin   the MenuPlugin for which the task is scheduled
     * @param startAt  the delay in milliseconds before the task is first executed
     * @param delay    the delay in milliseconds between successive task executions
     * @param consumer the BiConsumer to be executed with the TimerTask and a boolean indicating the task's success
     * @return the scheduled TimerTask
     */
    protected TimerTask scheduleFix(MenuPlugin plugin, long startAt, long delay, BiConsumer<TimerTask, Boolean> consumer) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    cancel();
                    consumer.accept(this, false);
                    return;
                }
                plugin.getScheduler().runTask(null, () -> consumer.accept(this, true));
            }
        };
        ZScheduler.TIMER.scheduleAtFixedRate(task, startAt, delay);
        return task;
    }


    protected <T> T randomElement(List<T> element) {
        if (element.size() == 0) return null;
        if (element.size() == 1) return element.get(0);
        Random random = new Random();
        return element.get(random.nextInt(element.size() - 1));
    }

    /**
     * @param message
     * @return
     */
    protected String color(String message) {
        if (message == null) return null;
        if (NMSUtils.isHexColor()) {
            Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
            Matcher matcher = pattern.matcher(message);
            while (matcher.find()) {
                String color = message.substring(matcher.start(), matcher.end());
                message = message.replace(color, String.valueOf(net.md_5.bungee.api.ChatColor.of(color)));
                matcher = pattern.matcher(message);
            }
        }
        return net.md_5.bungee.api.ChatColor.translateAlternateColorCodes('&', message);
    }

    protected String colorReverse(String message) {
        Pattern pattern = Pattern.compile(net.md_5.bungee.api.ChatColor.COLOR_CHAR + "x[a-fA-F0-9-" + net.md_5.bungee.api.ChatColor.COLOR_CHAR + "]{12}");
        Matcher matcher = pattern.matcher(message);
        while (matcher.find()) {
            String color = message.substring(matcher.start(), matcher.end());
            String colorReplace = color.replace("§x", "#");
            colorReplace = colorReplace.replace("§", "");
            message = message.replace(color, colorReplace);
            matcher = pattern.matcher(message);
        }

        return message.replace("§", "&");
    }

    /**
     * @param messages
     * @return
     */
    protected List<String> color(List<String> messages) {
        return messages.stream().map(this::color).collect(Collectors.toList());
    }

    /**
     * @param messages
     * @return
     */
    protected List<String> colorReverse(List<String> messages) {
        return messages.stream().map(this::colorReverse).collect(Collectors.toList());
    }

    /**
     * @param flagString
     * @return
     */
    protected ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }
        return null;
    }

    /**
     * @param list
     * @return
     */
    protected <T> List<T> reverse(List<T> list) {
        List<T> tmpList = new ArrayList<>();
        for (int index = list.size() - 1; index != -1; index--)
            tmpList.add(list.get(index));
        return tmpList;
    }

    /**
     * @param price
     * @return
     */
    protected String price(long price) {
        return String.format("%,d", price);
    }

    /**
     * Allows to generate a string
     *
     * @param length
     * @return
     */
    protected String generateRandomString(int length) {
        return new RandomString(length).nextString();
    }

    /**
     * @param message
     * @return
     */
    protected TextComponent buildTextComponent(String message) {
        return new TextComponent(message);
    }

    /**
     * @param component
     * @return
     */
    protected TextComponent setHoverMessage(TextComponent component, String... messages) {
        BaseComponent[] list = new BaseComponent[messages.length];
        for (int a = 0; a != messages.length; a++)
            list[a] = new TextComponent(messages[a] + (messages.length - 1 == a ? "" : "\n"));
        component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, list));
        return component;
    }

    /**
     * @param component
     * @return
     */
    protected TextComponent setHoverMessage(TextComponent component, List<String> messages) {
        if (messages.size() > 0) {
            BaseComponent[] list = new BaseComponent[messages.size()];
            for (int a = 0; a != messages.size(); a++) {
                list[a] = new TextComponent(messages.get(a) + (messages.size() - 1 == a ? "" : "\n"));
            }
            component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, list));
        }
        return component;
    }

    /**
     * @param component
     * @param action
     * @param command
     * @return
     */
    protected TextComponent setClickAction(TextComponent component, net.md_5.bungee.api.chat.ClickEvent.Action action, String command) {
        component.setClickEvent(new ClickEvent(action, command));
        return component;
    }

    /**
     * @param value
     * @return
     */
    protected String getDisplayBalance(double value) {
        if (value < 10000) return format(value, "#.#");
        else if (value < 1000000) return Integer.valueOf((int) (value / 1000)) + "k ";
        else if (value < 1000000000) return format((value / 1000) / 1000, "#.#") + "m ";
        else if (value < 1000000000000L) return Integer.valueOf((int) (((value / 1000) / 1000) / 1000)) + "M ";
        else return "to much";
    }

    /**
     * @param value
     * @return
     */
    protected String getDisplayBalance(long value) {
        if (value < 10000) return format(value, "#.#");
        else if (value < 1000000) return Integer.valueOf((int) (value / 1000)) + "k ";
        else if (value < 1000000000) return format((value / 1000) / 1000, "#.#") + "m ";
        else if (value < 1000000000000L) return Integer.valueOf((int) (((value / 1000) / 1000) / 1000)) + "M ";
        else return "to much";
    }

    /**
     * Allows you to count the number of items in inventory
     *
     * @param inventory
     * @param material
     * @return
     * @return
     */
    protected int count(org.bukkit.inventory.Inventory inventory, Material material) {
        int count = 0;
        for (ItemStack itemStack : inventory.getContents())
            if (itemStack != null && itemStack.getType().equals(material)) count += itemStack.getAmount();
        return count;
    }

    protected Enchantment enchantFromString(String str) {
        for (Enchantment enchantment : Enchantment.values())
            if (enchantment.getName().equalsIgnoreCase(str)) return enchantment;
        return null;
    }

    /**
     * @param direction
     * @return
     */
    protected BlockFace getClosestFace(float direction) {

        direction = direction % 360;

        if (direction < 0) direction += 360;

        direction = Math.round(direction / 45);

        switch ((int) direction) {
            case 0:
                return BlockFace.WEST;
            case 1:
                return BlockFace.NORTH_WEST;
            case 2:
                return BlockFace.NORTH;
            case 3:
                return BlockFace.NORTH_EAST;
            case 4:
                return BlockFace.EAST;
            case 5:
                return BlockFace.SOUTH_EAST;
            case 6:
                return BlockFace.SOUTH;
            case 7:
                return BlockFace.SOUTH_WEST;
            default:
                return BlockFace.WEST;
        }
    }

    /**
     * @param price
     * @return
     */
    protected String betterPrice(long price) {
        String betterPrice = "";
        String[] splitPrice = String.valueOf(price).split("");
        int current = 0;
        for (int a = splitPrice.length - 1; a > -1; a--) {
            current++;
            if (current > 3) {
                betterPrice = betterPrice.concat(".");
                current = 1;
            }
            betterPrice = betterPrice.concat(splitPrice[a]);
        }
        StringBuilder builder = new StringBuilder().append(betterPrice);
        builder.reverse();
        return builder.toString();
    }

    /**
     * @param enchantment
     * @param itemStack
     * @return
     */
    protected boolean hasEnchant(Enchantment enchantment, ItemStack itemStack) {
        return itemStack.hasItemMeta() && itemStack.getItemMeta().hasEnchants() && itemStack.getItemMeta().hasEnchant(enchantment);
    }

    /**
     * @param player
     * @param cooldown
     * @return
     */
    protected String timerFormat(Player player, String cooldown) {
        return TimerBuilder.getStringTime(CooldownBuilder.getCooldownPlayer(cooldown, player) / 1000);
    }

    /**
     * @param player
     * @param cooldown
     * @return
     */
    protected boolean isCooldown(Player player, String cooldown) {
        return isCooldown(player, cooldown, 0);
    }

    /**
     * @param player
     * @param cooldown
     * @param timer
     * @return
     */
    protected boolean isCooldown(Player player, String cooldown, int timer) {
        if (CooldownBuilder.isCooldown(cooldown, player)) {
            ActionBar.sendActionBar(player, String.format("§cVous devez attendre encore §6%s §cavant de pouvoir faire cette action.", timerFormat(player, cooldown)));
            return true;
        }
        if (timer > 0) CooldownBuilder.addCooldown(cooldown, player, timer);
        return false;
    }

    /**
     * @param list
     * @return
     */
    protected String toList(Stream<String> list) {
        return toList(list.collect(Collectors.toList()), "§e", "§6");
    }

    /**
     * @param list
     * @return
     */
    protected String toList(List<String> list) {
        return toList(list, "§e", "§6§n");
    }

    /**
     * @param list
     * @param color
     * @param color2
     * @return
     */
    protected String toList(List<String> list, String color, String color2) {
        if (list == null || list.size() == 0) return null;
        if (list.size() == 1) return list.get(0);
        String str = "";
        for (int a = 0; a != list.size(); a++) {
            if (a == list.size() - 1) str = str.concat(color + " " + Message.AND.getMessage() + " " + color2);
            else if (a != 0) str = str.concat(color + ", " + color2);
            str = str.concat(list.get(a));
        }
        return str;
    }

    /**
     * @param message
     * @return
     */
    protected String removeColor(String message) {
        for (ChatColor color : ChatColor.values())
            message = message.replace("§" + color.getChar(), "").replace("&" + color.getChar(), "");
        return message;
    }

    /**
     * @param l
     * @return
     */
    protected String format(long l) {
        return format(l, ' ');
    }

    /**
     * @param l
     * @param c
     * @return
     */
    protected String format(long l, char c) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(c);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(l);
    }

    /**
     * Permet d'obtenir la tête d'un joueur en utilisation le système de
     * configuration des inventaires
     *
     * @param itemStack
     * @param player
     * @return itemstack
     */
    public ItemStack playerHead(ItemStack itemStack, OfflinePlayer player) {

        String name = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : null;

        if (NmsVersion.nmsVersion.isNewMaterial()) {
            if (itemStack.getType().equals(Material.PLAYER_HEAD) && name != null && name.startsWith("HEAD")) {
                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                name = name.replace("HEAD", "");
                if (name.length() == 0) {
                    meta.setDisplayName(null);
                } else {
                    meta.setDisplayName(name);
                }
                meta.setOwningPlayer(player);
                itemStack.setItemMeta(meta);
            }
        } else {
            if (itemStack.getType().equals(getMaterial(397)) && itemStack.getData().getData() == 3 && name != null && name.startsWith("HEAD")) {
                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                name = name.replace("HEAD", "");
                if (name.length() == 0) {
                    meta.setDisplayName(null);
                } else {
                    meta.setDisplayName(name);
                }
                meta.setOwner(player.getName());
                itemStack.setItemMeta(meta);
            }
        }
        return itemStack;
    }

    /**
     * Allows you to get an itemstack to create a player's head
     *
     * @return itemstack
     */
    protected ItemStack playerHead() {
        return NmsVersion.nmsVersion.isNewMaterial() ? new ItemStack(Material.PLAYER_HEAD) : new ItemStack(getMaterial(397), 1, (byte) 3);
    }

    /**
     * Allows to obtain a class according to the provider
     *
     * @param plugin
     * @param classz
     * @return T
     */
    protected <T> T getProvider(Plugin plugin, Class<T> classz) {
        RegisteredServiceProvider<T> provider = plugin.getServer().getServicesManager().getRegistration(classz);
        if (provider == null) return null;
        return provider.getProvider();
    }

    /**
     * @param configuration
     * @return
     */
    protected PotionEffectType getPotion(String configuration) {
        for (PotionEffectType effectType : PotionEffectType.values()) {
            if (effectType.getName().equalsIgnoreCase(configuration)) {
                return effectType;
            }
        }
        return null;
    }

    /**
     * Allows to execute a runnable in an asynmetrical way
     *
     * @param runnable
     */
    protected void runAsync(Plugin plugin, Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
    }

    /**
     * Turns back time for a human
     *
     * @param second
     * @return string
     */
    protected String getStringTime(long second) {
        return TimerBuilder.getStringTime(second);
    }

    // Source: https://blog.jeff-media.com/creating-custom-heads-in-spigot-1-18-1/

    /**
     * Allows you to create a head from a URL
     *
     * @param url
     * @return itemstack
     */
    protected ItemStack createSkull(String url) {

        if (url == null) return null;

        ItemStack head = playerHead();
        if (url.isEmpty()) {
            return head;
        }

        if (NMSUtils.isNewHeadApi()) {
            this.applyTextureUrl(head, url);
        } else {
            this.applyTexture(head, url);
        }

        return head;
    }

    private void applyTextureUrl(ItemStack itemStack, String url) {
        SkullMeta headMeta = (SkullMeta) itemStack.getItemMeta();
        if (headMeta != null) {
            Object result = cache.get(url, () -> getProfile(url));
            if (result instanceof PlayerProfile) {
                headMeta.setOwnerProfile((PlayerProfile) result);
            }
        }
        itemStack.setItemMeta(headMeta);
    }

    private PlayerProfile getProfile(String url) {
        PlayerProfile profile = Bukkit.createPlayerProfile(RANDOM_UUID); // Get a new player profile
        PlayerTextures textures = profile.getTextures();
        URL urlObject;
        try {
            // urlObject = new URL(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
            urlObject = getUrlFromBase64(url); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
            return null;
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
    }

    public URL getUrlFromBase64(String base64) throws MalformedURLException {
        String decoded;
        try {
            decoded = new String(Base64.getDecoder().decode(base64));
            decoded = decoded.substring("{\"textures\":{\"SKIN\":{\"url\":\"".length(), decoded.length() - "\"}}}".length());
        } catch (IllegalArgumentException exception) {
            // If the base64 is not valid, try to assume it's a simple URL
            decoded = base64;
        }
        return new URL(decoded);
    }

    protected void applyTexture(ItemStack itemStack, String url) {
        SkullMeta headMeta = (SkullMeta) itemStack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), "zmenu_head");

        profile.getProperties().put("textures", new Property("textures", url));

        try {
            Field profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);

        } catch (IllegalArgumentException | NoSuchFieldException | SecurityException | IllegalAccessException error) {
            error.printStackTrace();
        }
        itemStack.setItemMeta(headMeta);
    }

    /**
     * Allows to check if an itemstack and a head
     *
     * @param itemStack
     * @return boolean
     */
    protected boolean isPlayerHead(ItemStack itemStack) {
        Material material = itemStack.getType();
        if (NmsVersion.nmsVersion.isNewMaterial()) return material.equals(Material.PLAYER_HEAD);
        return (material.equals(getMaterial(397))) && (itemStack.getDurability() == 3);
    }

    /**
     * @param object
     * @param field
     * @return
     * @throws SecurityException
     * @throws NoSuchFieldException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     */
    protected Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? NmsVersion.nmsVersion.isNewMaterial() ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field) : null;
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

    /**
     * Unregister a bukkit command
     *
     * @param plugin
     * @param command
     */
    protected void unRegisterBukkitCommand(Plugin plugin, PluginCommand command) {
        try {
            Object result = getPrivateField(plugin.getServer().getPluginManager(), "commandMap");
            SimpleCommandMap commandMap = (SimpleCommandMap) result;

            Object map = getPrivateField(commandMap, "knownCommands");
            @SuppressWarnings("unchecked") HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(command.getName());
            for (String alias : command.getAliases()) {
                knownCommands.remove(alias);
            }
            knownCommands.remove(plugin.getDescription().getName() + ":" + command.getName());
            for (String alias : command.getAliases()) {
                knownCommands.remove(plugin.getDescription().getName() + ":" + alias);
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Create a progress bar
     * https://www.spigotmc.org/threads/progress-bars-and-percentages.276020/
     *
     * @param current
     * @param max
     * @param totalBars
     * @param symbol
     * @param completedColor
     * @param notCompletedColor
     * @return string
     */
    public String getProgressBar(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat(completedColor + symbol, progressBars) + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars);
    }

    /**
     * Create a progress bar
     *
     * @param current
     * @param max
     * @param progressBar
     * @return string
     */
    public String getProgressBar(int current, int max, ProgressBar progressBar) {
        return this.getProgressBar(current, max, progressBar.getLenght(), progressBar.getSymbol(), progressBar.getCompletedColor(), progressBar.getNotCompletedColor());
    }

    /**
     * Allows you to check if an inventory will contain armor or items
     *
     * @param player
     * @return boolean
     */
    protected boolean inventoryHasItem(Player player) {

        ItemStack itemStack = player.getInventory().getBoots();
        if (itemStack != null) {
            return true;
        }

        itemStack = player.getInventory().getChestplate();
        if (itemStack != null) {
            return true;
        }

        itemStack = player.getInventory().getLeggings();
        if (itemStack != null) {
            return true;
        }

        itemStack = player.getInventory().getHelmet();
        if (itemStack != null) {
            return true;
        }

        for (ItemStack itemStack1 : player.getInventory().getContents()) {
            if (itemStack1 != null) {
                return true;
            }
        }

        return false;
    }

    public String getFileNameWithoutExtension(File file) {
        Pattern pattern = Pattern.compile("(?<=.)\\.[^.]+$");
        return pattern.matcher(file.getName()).replaceAll("").replace(" ", "_");
    }

}
