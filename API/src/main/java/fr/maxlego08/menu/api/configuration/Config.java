package fr.maxlego08.menu.api.configuration;


import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Config {

    // Enable debug, allows you to display errors in the console that would normally be hidden.
    public static boolean enableDebug = false;

    // Enable debug time, allows you to display the code execution time in nanosecond, perfect for testing the effectiveness of the plugin.
    public static boolean enableDebugTime= false;

    // Enable an information message, allows you to view messages that tell you about an inventory or that an order has been successfully loaded.
    public static boolean enableInformationMessage= true;

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

    public static boolean enableUpdateCheck = true;

    public boolean isUpdated = false;

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

    public void load(YamlConfiguration config, File file) {

        enableDebug = getOrAdd(config,"enable-debug",false);

        enableDebugTime = getOrAdd(config,"enable-debug-time",false);

        enableInformationMessage = getOrAdd(config,"enable-information-message",true);

        enableLogStorageFile = getOrAdd(config, "enable-log-storage-file", false);
        enableOpenMessage = getOrAdd(config, "enable-open-message",true);
        enableMiniMessageFormat = getOrAdd(config,"enable-mini-message-format", true);
        enablePlayerCommandInChat = getOrAdd(config, "enable-player-command-in-chat", false);
        enableFastEvent = getOrAdd(config,"enable-fast-event", false);

        secondsSavePlayerData = getOrAdd(config,"seconds-save-player-data",600);
        secondsSavePlayerInventories = getOrAdd(config, "seconds-save-player-inventories", 600);
        
        mainMenu = getOrAdd(config, "main-menu", "example");
        useSwapItemOffHandKeyToOpenMainMenu = getOrAdd(config,"use-swap-item-off-hand-key-to-open-main-menu", false);
        useSwapItemOffHandKeyToOpenMainMenuNeedsShift = getOrAdd(config,"use-swap-item-off-hand-key-to-open-main-menu-needs-shift", false);

        specifyPathMenus = getOrAdd(config,"specify-path-menus", new ArrayList<>());
        generateDefaultFile = getOrAdd(config,"generate-default-file", true);
        disableDoubleClickEvent = getOrAdd(config,"disable-double-click-event", true);

        enableAntiDupe = getOrAdd(config,"enable-anti-dupe", true);
        enableAntiDupeDiscordNotification = getOrAdd(config,"enable-anti-dupe-discord-notification", false);
        antiDupeDiscordWebhookUrl = getOrAdd(config,"anti-dupe-discord-webhook-url", "https://discord.com/api/webhooks/<your discord webhook url>");
        antiDupeMessage = getOrAdd(config,"anti-dupe-message", "**%player%** use %amount% %itemname% which comes from zMenu. Removing it !");

        allClicksType = getOrAdd(config,"all-clicks-type",Arrays.asList("MIDDLE","RIGHT","LEFT","SHIFT_RIGHT","SHIFT_LEFT")).stream()
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

        enableCacheItemStack = getOrAdd(config,"enable-cache-item-stack", true);
        enableCooldownClick = getOrAdd(config,"enable-cooldown-click", true);
        cooldownClickMilliseconds = getOrAdd(config,"cooldown-click-milliseconds", (long) 100);

        cachePlaceholderAPI = getOrAdd(config, "cache-placeholder-api",(long) 20);
        enableCachePlaceholderAPI = getOrAdd(config,"enable-cache-placeholder-api", false);

        enableDownloadCommand = getOrAdd(config,"enable-download-command", false);
        enablePlayerOpenInventoryLogs = getOrAdd(config,"enable-player-open-inventory-logs", true);

        enableUpdateCheck = getOrAdd(config,"enable-update-check", true);

        if (isUpdated){
            try {
                config.save(file);
            } catch (Exception e) {
                Bukkit.getLogger().warning("[zMenu] Failed to save config file: " + file.getName()+ ". Exception: " + e.getMessage());
            }
        }
        isUpdated = false;
    }


    private <T> T getOrAdd(YamlConfiguration yamlConfiguration, String path, T defaultValue) {
        if (!yamlConfiguration.contains(path)) {
            yamlConfiguration.set(path, defaultValue);
            isUpdated = true;
            return defaultValue;
        }
        Object value = yamlConfiguration.get(path);
        if (defaultValue != null && defaultValue.getClass().isInstance(value)) {
            return (T) value;
        }
        return defaultValue;
    }
}
