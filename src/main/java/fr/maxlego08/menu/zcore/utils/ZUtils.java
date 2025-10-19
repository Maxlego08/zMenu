package fr.maxlego08.menu.zcore.utils;

import com.google.common.base.Charsets;
import com.google.common.base.Strings;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.SimpleCache;
import fr.maxlego08.menu.zcore.SkinUrlDecoder;
import fr.maxlego08.menu.zcore.enums.EnumInventory;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.builder.CooldownBuilder;
import fr.maxlego08.menu.zcore.utils.builder.TimerBuilder;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.nms.NmsVersion;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.io.*;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public abstract class ZUtils extends MessageUtils {

    private static final Timer TIMER = new Timer();
    private static final UUID RANDOM_UUID = UUID.fromString("92864445-51c5-4c3b-9039-517c9927d1b4"); // We reuse the same "random" UUID all the time
    private static final SimpleCache<String, Object> cache = new SimpleCache<>();
    private static final ConcurrentHashMap<Path, CachedLines> CONFIGURATION_CACHE = new ConcurrentHashMap<>();
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

    /**
     * Calculates the inventory size based on the provided matrix.
     * <p>
     * The inventory size is determined by the number of lines in the matrix,
     * each line representing 9 slots. The maximum inventory size is capped at 54.
     *
     * @param matrix the list of strings representing the matrix
     * @return the calculated inventory size, which is the lesser of
     * the matrix size times 9 or 54
     */
    protected int getInventorySizeByMatrix(List<String> matrix) {
        return Math.min(matrix.size() * 9, 54);
    }

    /**
     * Generates a matrix from a list of strings.
     * <p>
     * The matrix must have exactly 6 lines, and each line must have exactly 9 characters.
     * <p>
     * The characters in the matrix are used to associate a slot in the inventory with a character.
     * <p>
     * Each character is associated with a list of slots.
     * <p>
     * If the matrix is wrong, an error message is sent to the logger and an empty map is returned.
     *
     * @param matrix the matrix to generate
     * @return a map of characters to slots
     */
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

    protected void give(Player player, ItemStack item) {
        if (hasInventoryFull(player)) player.getWorld().dropItem(player.getLocation(), item);
        else player.getInventory().addItem(item);
    }

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

    protected boolean same(ItemStack itemStack, String name) {
        return this.hasDisplayName(itemStack) && itemStack.getItemMeta().getDisplayName().equals(name);
    }

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
     * Creates an inventory for the specified plugin and player with the given inventory type.
     *
     * @param plugin    the MenuPlugin for which the inventory is created
     * @param player    the Player for whom the inventory is created
     * @param inventory the type of EnumInventory to be created
     */
    protected void createInventory(ZMenuPlugin plugin, Player player, EnumInventory inventory) {
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
    protected void createInventory(ZMenuPlugin plugin, Player player, EnumInventory inventory, int page) {
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
    protected void createInventory(ZMenuPlugin plugin, Player player, EnumInventory inventory, int page, Object... objects) {
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
    protected void createInventory(ZMenuPlugin plugin, Player player, int inventory, int page, Object... objects) {
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
    protected TimerTask scheduleFix(ZMenuPlugin plugin, long delay, BiConsumer<TimerTask, Boolean> consumer) {
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
    protected TimerTask scheduleFix(ZMenuPlugin plugin, long startAt, long delay, BiConsumer<TimerTask, Boolean> consumer) {
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    cancel();
                    consumer.accept(this, false);
                    return;
                }
                plugin.getScheduler().runNextTick(w -> consumer.accept(this, true));
            }
        };
        TIMER.scheduleAtFixedRate(task, startAt, delay);
        return task;
    }

    protected <T> T randomElement(List<T> element) {
        if (element.isEmpty()) return null;
        if (element.size() == 1) return element.getFirst();
        Random random = new Random();
        return element.get(random.nextInt(element.size() - 1));
    }

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

    public String colorReverse(String message) {
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

    protected List<String> color(List<String> messages) {
        List<String> colored = new ArrayList<>(messages.size());
        for (String message : messages) {
            colored.add(this.color(message));
        }
        return colored;
    }

    public List<String> colorReverse(List<String> messages) {
        List<String> colored = new ArrayList<>(messages.size());
        for (String message : messages) {
            colored.add(this.colorReverse(message));
        }
        return colored;
    }

    public ItemFlag getFlag(String flagString) {
        for (ItemFlag flag : ItemFlag.values()) {
            if (flag.name().equalsIgnoreCase(flagString)) return flag;
        }
        return null;
    }

    protected String generateRandomString(int length) {
        return new RandomString(length).nextString();
    }

    protected TextComponent buildTextComponent(String message) {
        return new TextComponent(message);
    }

    protected TextComponent setHoverMessage(TextComponent component, List<String> messages) {
        if (!messages.isEmpty()) {
            BaseComponent[] list = new BaseComponent[messages.size()];
            for (int a = 0; a != messages.size(); a++) {
                list[a] = new TextComponent(messages.get(a) + (messages.size() - 1 == a ? "" : "\n"));
            }
            component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, list));
        }
        return component;
    }

    protected TextComponent setClickAction(TextComponent component, net.md_5.bungee.api.chat.ClickEvent.Action action, String command) {
        component.setClickEvent(new ClickEvent(action, command));
        return component;
    }

    protected String timerFormat(Player player, String cooldown) {
        return TimerBuilder.getStringTime(CooldownBuilder.getCooldownPlayer(cooldown, player) / 1000);
    }

    protected boolean isCooldown(Player player, String cooldown) {
        return isCooldown(player, cooldown, 0);
    }

    protected boolean isCooldown(Player player, String cooldown, int timer) {
        if (CooldownBuilder.isCooldown(cooldown, player)) {
            ActionBar.sendActionBar(player, String.format("§cVous devez attendre encore §6%s §cavant de pouvoir faire cette action.", timerFormat(player, cooldown)));
            return true;
        }
        if (timer > 0) CooldownBuilder.addCooldown(cooldown, player, timer);
        return false;
    }

    protected String toList(Stream<String> list) {
        List<String> values = new ArrayList<>();
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            values.add(iterator.next());
        }
        return toList(values, "§e", "§6");
    }

    protected String toList(List<String> list) {
        return toList(list, "§e", "§6§n");
    }

    protected String toList(List<String> list, String color, String color2) {
        if (list == null || list.isEmpty()) return null;
        if (list.size() == 1) return list.getFirst();
        String str = "";
        for (int a = 0; a != list.size(); a++) {
            if (a == list.size() - 1) str = str.concat(color + " " + Message.AND.getMessage() + " " + color2);
            else if (a != 0) str = str.concat(color + ", " + color2);
            str = str.concat(list.get(a));
        }
        return str;
    }

    protected String format(long l) {
        return format(l, ' ');
    }

    protected String format(long l, char c) {
        DecimalFormat formatter = (DecimalFormat) NumberFormat.getInstance(Locale.US);
        DecimalFormatSymbols symbols = formatter.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(c);
        formatter.setDecimalFormatSymbols(symbols);
        return formatter.format(l);
    }

    public ItemStack playerHead(ItemStack itemStack, OfflinePlayer player) {

        String name = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : null;

        if (NmsVersion.nmsVersion.isNewMaterial()) {
            if (itemStack.getType().equals(Material.PLAYER_HEAD) && name != null && name.startsWith("HEAD")) {
                SkullMeta meta = (SkullMeta) itemStack.getItemMeta();
                name = name.replace("HEAD", "");
                if (name.isEmpty()) {
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
                if (name.isEmpty()) {
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
            urlObject = SkinUrlDecoder.extractSkinUrl(url).toURL(); // The URL to the skin, for example: https://textures.minecraft.net/texture/18813764b2abc94ec3c3bc67b9147c21be850cdf996679703157f4555997ea63a
        } catch (URISyntaxException | MalformedURLException exception) {
            exception.printStackTrace();
            return null;
        }
        textures.setSkin(urlObject); // Set the skin of the player profile to the URL
        profile.setTextures(textures); // Set the textures back to the profile
        return profile;
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

    protected boolean isPlayerHead(ItemStack itemStack) {
        Material material = itemStack.getType();
        if (NmsVersion.nmsVersion.isNewMaterial()) return material.equals(Material.PLAYER_HEAD);
        return (material.equals(getMaterial(397))) && (itemStack.getDurability() == 3);
    }

    protected Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Class<?> clazz = object.getClass();
        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? NmsVersion.nmsVersion.isNewMaterial() ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field) : null;
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }

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

    public String getProgressBar(int current, int max, int totalBars, char symbol, String completedColor, String notCompletedColor) {
        float percent = (float) current / max;
        int progressBars = (int) (totalBars * percent);

        return Strings.repeat(completedColor + symbol, progressBars) + Strings.repeat(notCompletedColor + symbol, totalBars - progressBars);
    }

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

    /**
     * Given a file, this method returns the name of the file without the extension.
     * Replaces any spaces in the name with underscores.
     *
     * @param file the file
     * @return the name of the file without the extension.
     */
    public String getFileNameWithoutExtension(File file) {
        Pattern pattern = Pattern.compile("(?<=.)\\.[^.]+$");
        return pattern.matcher(file.getName()).replaceAll("").replace(" ", "_");
    }

    protected YamlConfiguration loadAndReplaceConfiguration(File file, Map<String, Object> mapPlaceholders) {
        YamlConfiguration loadConfiguration = new YamlConfiguration();

        try {
            CachedLines cachedLines = getCachedLines(file);
            StringBuilder builder = new StringBuilder();
            Placeholders placeholders = new Placeholders();
            Map<String, Object> placeholdersMap = mapPlaceholders != null ? mapPlaceholders : Collections.emptyMap();

            for (String originalLine : cachedLines.lines()) {
                String line = originalLine;

                for (Map.Entry<String, Object> replacement : placeholdersMap.entrySet()) {
                    String key = replacement.getKey();
                    Object value = replacement.getValue();

                    if (line != null) {
                        if (value instanceof List<?> && line.contains("%" + key + "%")) {
                            int index = line.indexOf("%" + key + "%");
                            String prefix = line.substring(0, index);
                            String finalLine = line.substring(index);
                            ((List<?>) value).forEach(currentValue -> {
                                String replacementValue = currentValue != null ? currentValue.toString() : "";
                                String currentElement = placeholders.parse(finalLine, key, replacementValue);
                                builder.append(placeholders.parse(prefix, key, replacementValue)).append(currentElement);
                                builder.append('\n');
                            });

                            line = null;
                        } else {
                            String replacementValue = value != null ? value.toString() : "";
                            line = placeholders.parse(line, key, replacementValue);
                        }
                    }
                }

                if (line != null) {
                    builder.append(line);
                    builder.append('\n');
                }
            }

            loadConfiguration.loadFromString(builder.toString());

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return loadConfiguration;
    }

    protected static void clearConfigurationCache() {
        CONFIGURATION_CACHE.clear();
    }

    private static CachedLines getCachedLines(File file) throws IOException {
        Path path = file.toPath();
        long lastModified = file.lastModified();
        CachedLines cachedLines = CONFIGURATION_CACHE.get(path);

        if (cachedLines == null || cachedLines.lastModified() != lastModified) {
            List<String> lines = Files.readAllLines(path, Charsets.UTF_8);
            cachedLines = new CachedLines(lines, lastModified);
            CONFIGURATION_CACHE.put(path, cachedLines);
        }

        return cachedLines;
    }

    private record CachedLines(List<String> lines, long lastModified) {
        private CachedLines {
            lines = List.copyOf(lines);
        }
    }

}
