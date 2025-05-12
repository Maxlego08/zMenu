package fr.maxlego08.menu.api.configuration;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

    // Enable debug, allows you to display errors in the console that would normally be hidden.
    public static boolean enableDebug = false;

    // Enable debug time, allows you to display the code execution time in nanosecond, perfect for testing the effectiveness of the plugin.
    public static boolean enableDebugTime = false;

    // Enable an information message, allows you to view messages that tell you about an inventory or that an order has been successfully loaded.
    public static boolean enableInformationMessage = true;

    // Enable save or load file log in console
    public static boolean enableLogStorageFile = false;

    // Enable open message, default value for the command /zm open <inventory name> <player> <display message>
    public static boolean enableOpenMessage = true;

    // Enable mini message format, allows you to activate the mini message format, available from 1.17 onwards, more information here: https://docs.advntr.dev/minimessage/index.html
    public static boolean enableMiniMessageFormat = true;

    // Enable player command in chat, Allows you to ensure that when a player executes a command, they execute it from the chat and not from the console. If you have "fake" command, which are not saved in spigot you need to enable this option.
    public static boolean enablePlayerCommandInChat = false;

    // Allows you to use the FastEvent interface instead of bukkit events. You gain performance. To use FastEvent, please read the documentation.
    public static boolean enableFastEvent = false;

    // Seconds save player data: The time in seconds for automatic backup of player data.
    public static int secondsSavePlayerData = 600;

    // Seconds save player data: The time in seconds for automatic backup of inventories data.
    public static int secondsSavePlayerInventories = 600;

    // Auto save file inventory on update: allows you to save the file of players inventories automatically.
    public static boolean autoSaveFileInventoryOnUpdate = true;

    // Default menu name
    public static String mainMenu = "example";

    // Open main menu when swap item offhand key is press
    public static boolean useSwapItemOffHandKeyToOpenMainMenu = false;

    // Open main menu when swap item offhand key is press and sneak key
    public static boolean useSwapItemOffHandKeyToOpenMainMenuNeedsShift = false;

    // Load specific inventories
    public static List<String> specifyPathMenus = new ArrayList<>();

    // Generate default configuration
    public static boolean generateDefaultFile = true;

    // Does not take double click into account
    public static boolean disableDoubleClickEvent = true;

    // Enable anti dupe
    public static boolean enableAntiDupe = true;
    public static boolean enableAntiDupeDiscordNotification = false;
    public static String antiDupeDiscordWebhookUrl = "https://discord.com/api/webhooks/<your discord webhook url>";
    public static String antiDupeMessage = "**%player%** use %amount% %itemname% which comes from zMenu. Removing it !";

    public static List<ClickType> allClicksType = Arrays.asList(ClickType.MIDDLE, ClickType.RIGHT, ClickType.LEFT, ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT);
    public static boolean enableCacheItemStack = true;

    public static boolean enableCooldownClick = true;
    public static long cooldownClickMilliseconds = 100;
    public static long cachePlaceholderAPI = 20;
    public static boolean enableCachePlaceholderAPI = false;
    public static boolean enableDownloadCommand = false;
    public static boolean enablePlayerOpenInventoryLogs = true;

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;

    /**
     * Private constructor for singleton.
     */
    private Config() {
    }

    /**
     * Return a singleton instance of Config.
     */
    public static Config getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Config.class) {
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public void load(FileConfiguration configuration) {

        enableDebug = configuration.getBoolean("enable-debug");
        enableDebugTime = configuration.getBoolean("enable-debug-time");
        enableInformationMessage = configuration.getBoolean("enable-information-message");
        enableLogStorageFile = configuration.getBoolean("enable-log-storage-file");
        enableOpenMessage = configuration.getBoolean("enable-open-message");
        enableMiniMessageFormat = configuration.getBoolean("enable-mini-message-format");
        enablePlayerCommandInChat = configuration.getBoolean("enable-player-command-in-chat");
        enableFastEvent = configuration.getBoolean("enable-fast-event");

        secondsSavePlayerData = configuration.getInt("seconds-save-player-data");
        secondsSavePlayerInventories = configuration.getInt("seconds-save-player-inventories");

        autoSaveFileInventoryOnUpdate = configuration.getBoolean("auto-save-file-inventory-on-update");
        mainMenu = configuration.getString("main-menu");
        useSwapItemOffHandKeyToOpenMainMenu = configuration.getBoolean("use-swap-item-off-hand-key-to-open-main-menu");
        useSwapItemOffHandKeyToOpenMainMenuNeedsShift = configuration.getBoolean("use-swap-item-off-hand-key-to-open-main-menu-needs-shift");

        specifyPathMenus = configuration.getStringList("specify-path-menus");
        generateDefaultFile = configuration.getBoolean("generate-default-file");
        disableDoubleClickEvent = configuration.getBoolean("disable-double-click-event");

        enableAntiDupe = configuration.getBoolean("enable-anti-dupe");
        enableAntiDupeDiscordNotification = configuration.getBoolean("enable-anti-dupe-discord-notification");
        antiDupeDiscordWebhookUrl = configuration.getString("anti-dupe-discord-webhook-url");
        antiDupeMessage = configuration.getString("anti-dupe-message");

        allClicksType = configuration.getStringList("all-clicks-type").stream()
                .map(name -> {
                    try {
                        return ClickType.valueOf(name);
                    } catch (IllegalArgumentException e) {
                        Bukkit.getLogger().warning("[zMenu] Invalid click type in config: " + name);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        enableCacheItemStack = configuration.getBoolean("enable-cache-item-stack");
        enableCooldownClick = configuration.getBoolean("enable-cooldown-click");
        cooldownClickMilliseconds = configuration.getLong("cooldown-click-milliseconds");

        cachePlaceholderAPI = configuration.getLong("cache-placeholder-api");
        enableCachePlaceholderAPI = configuration.getBoolean("enable-cache-placeholder-api");

        enableDownloadCommand = configuration.getBoolean("enable-download-command");
        enablePlayerOpenInventoryLogs = configuration.getBoolean("enable-player-open-inventory-logs");
    }

}
