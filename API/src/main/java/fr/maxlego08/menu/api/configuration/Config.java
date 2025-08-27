package fr.maxlego08.menu.api.configuration;

import fr.maxlego08.menu.api.configuration.annotation.ConfigDialog;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.DialogInputType;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@ConfigDialog(
    name = "zMenu Config",
    externalTitle = "zMenu Configuration"
)
public class Config {

    // Enable debug, allows you to display errors in the console that would normally be hidden.
    @ConfigOption(
        key = "enableDebug",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable debug"
    )
    public static boolean enableDebug = false;

    // Enable debug time, allows you to display the code execution time in nanosecond, perfect for testing the effectiveness of the plugin.
    @ConfigOption(
        key = "enableDebugTime",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable debug time"
    )
    public static boolean enableDebugTime = false;

    // Enable an information message, allows you to view messages that tell you about an inventory or that an order has been successfully loaded.
    @ConfigOption(
        key = "enableInformationMessage",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable information message"
    )
    public static boolean enableInformationMessage = true;

    // Enable save or load file log in console
    @ConfigOption(
        key = "enableLogStorageFile",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable log storage file"
    )
    public static boolean enableLogStorageFile = false;

    // Skip update check
    @ConfigOption(
        key = "skipUpdateCheck",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Skip update check"
    )
    public static boolean skipUpdateCheck = false;

    // Enable open message, default value for the command /zm open <inventory name> <player> <display message>
    @ConfigOption(
        key = "enableOpenMessage",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable open message"
    )
    public static boolean enableOpenMessage = true;

    // Enable mini message format, allows you to activate the mini message format, available from 1.17 onwards, more information here: https://docs.advntr.dev/minimessage/index.html
    @ConfigOption(
        key = "enableMiniMessageFormat",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable mini message format"
    )
    public static boolean enableMiniMessageFormat = true;

    // Enable player command in chat, Allows you to ensure that when a player executes a command, they execute it from the chat and not from the console. If you have "fake" command, which are not saved in spigot you need to enable this option.
    @ConfigOption(
        key = "enablePlayerCommandInChat",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable player command in chat"
    )
    public static boolean enablePlayerCommandInChat = false;

    // Allows you to use the FastEvent interface instead of bukkit events. You gain performance. To use FastEvent, please read the documentation.
    @ConfigOption(
        key = "enableFastEvent",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable FastEvent"
    )
    public static boolean enableFastEvent = false;

    // Seconds save player data: The time in seconds for automatic backup of player data.
    @ConfigOption(
        key = "secondsSavePlayerData",
        type = DialogInputType.NUMBER_RANGE,
        label = "Seconds save player data",
        startRange = 60,
        endRange = 3600,
        stepRange = 5
    )
    public static int secondsSavePlayerData = 600;

    // Seconds save player data: The time in seconds for automatic backup of inventories data.
    @ConfigOption(
        key = "secondsSavePlayerInventories",
        type = DialogInputType.NUMBER_RANGE,
        label = "Seconds save player inventories",
        startRange = 60,
        endRange = 3600,
        stepRange = 5
    )
    public static int secondsSavePlayerInventories = 600;

    // Default menu name
    public static String mainMenu = "example";

    // Open main menu when swap item offhand key is press
    @ConfigOption(
        key = "useSwapItemOffHandKeyToOpenMainMenu",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Use swap item offhand key to open main menu"
    )
    public static boolean useSwapItemOffHandKeyToOpenMainMenu = false;

    // Open main menu when swap item offhand key is press and sneak key
    @ConfigOption(
        key = "useSwapItemOffHandKeyToOpenMainMenuNeedsShift",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Use swap item offhand key to open main menu needs shift"
    )
    public static boolean useSwapItemOffHandKeyToOpenMainMenuNeedsShift = false;

    // Load specific inventories
    public static List<String> specifyPathMenus = new ArrayList<>();

    // Generate default configuration
    @ConfigOption(
        key = "generateDefaultFile",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Generate default file"
    )
    public static boolean generateDefaultFile = true;

    // Does not take double click into account
    @ConfigOption(
        key = "disableDoubleClickEvent",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Disable double click event"
    )
    public static boolean disableDoubleClickEvent = true;

    // Enable anti dupe
    @ConfigOption(
        key = "enableAntiDupe",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable anti dupe"
    )
    public static boolean enableAntiDupe = true;
    // Enable anti dupe discord notification
    @ConfigOption(
        key = "enableAntiDupeDiscordNotification",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable anti dupe discord notification"
    )
    public static boolean enableAntiDupeDiscordNotification = false;
    @ConfigOption(
        key = "antiDupeDiscordWebhookUrl",
        type = DialogInputType.TEXT,
        label = "Anti dupe discord webhook url",
        maxLength = 100
    )
    public static String antiDupeDiscordWebhookUrl = "https://discord.com/api/webhooks/<your discord webhook url>";
    @ConfigOption(
        key = "antiDupeMessage",
        type = DialogInputType.TEXT,
        label = "Anti dupe message",
        maxLength = 200
    )
    public static String antiDupeMessage = "**%player%** use %amount% %itemname% which comes from zMenu. Removing it !";

    public static List<ClickType> allClicksType = Arrays.asList(ClickType.MIDDLE, ClickType.RIGHT, ClickType.LEFT, ClickType.SHIFT_RIGHT, ClickType.SHIFT_LEFT);
    // Enable cache itemstack in memory
    @ConfigOption(
        key = "enableCacheItemStack",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable cache item stack"
    )
    public static boolean enableCacheItemStack = true;

    @ConfigOption(
        key = "enableCooldownClick",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable cooldown click"
    )

    public static boolean enableCooldownClick = true;
    @ConfigOption(
        key = "cooldownClickMilliseconds",
        type = DialogInputType.NUMBER_RANGE,
        label = "Cooldown click milliseconds",
        endRange = 1000,
        stepRange = 10
    )

    public static long cooldownClickMilliseconds = 100;
    @ConfigOption(
        key = "cachePlaceholderAPI",
        type = DialogInputType.NUMBER_RANGE,
        label = "Cache PlaceholderAPI",
        endRange = 300,
        stepRange = 5
    )
    public static long cachePlaceholderAPI = 20;

    @ConfigOption(
        key = "enableCachePlaceholderAPI",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable cache PlaceholderAPI"
    )
    public static boolean enableCachePlaceholderAPI = false;
    @ConfigOption(
        key = "enableDownloadCommand",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable download command"
    )
    public static boolean enableDownloadCommand = false;
    @ConfigOption(
        key = "enablePlayerOpenInventoryLogs",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable player open inventory logs"
    )
    public static boolean enablePlayerOpenInventoryLogs = true;

    /**
     * static Singleton instance.
     */
    private static volatile Config instance;

    public static boolean updated = false;

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

        enableDebug = configuration.getBoolean(ConfigPath.ENABLE_DEBUG.getPath());
        enableDebugTime = configuration.getBoolean(ConfigPath.ENABLE_DEBUG_TIME.getPath());
        enableInformationMessage = configuration.getBoolean(ConfigPath.ENABLE_INFORMATION_MESSAGE.getPath());
        enableLogStorageFile = configuration.getBoolean(ConfigPath.ENABLE_LOG_STORAGE_FILE.getPath());
        skipUpdateCheck = configuration.getBoolean(ConfigPath.SKIP_UPDATE_CHECK.getPath());
        enableOpenMessage = configuration.getBoolean(ConfigPath.ENABLE_OPEN_MESSAGE.getPath());
        enableMiniMessageFormat = configuration.getBoolean(ConfigPath.ENABLE_MINI_MESSAGE_FORMAT.getPath());
        enablePlayerCommandInChat = configuration.getBoolean(ConfigPath.ENABLE_PLAYER_COMMAND_IN_CHAT.getPath());
        enableFastEvent = configuration.getBoolean(ConfigPath.ENABLE_FAST_EVENT.getPath());

        secondsSavePlayerData = configuration.getInt(ConfigPath.SECONDS_SAVE_PLAYER_DATA.getPath());
        secondsSavePlayerInventories = configuration.getInt(ConfigPath.SECONDS_SAVE_PLAYER_INVENTORIES.getPath());
        
        mainMenu = configuration.getString(ConfigPath.MAIN_MENU.getPath());
        useSwapItemOffHandKeyToOpenMainMenu = configuration.getBoolean(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU.getPath());
        useSwapItemOffHandKeyToOpenMainMenuNeedsShift = configuration.getBoolean(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU_NEEDS_SHIFT.getPath());

        specifyPathMenus = configuration.getStringList(ConfigPath.SPECIFY_PATH_MENUS.getPath());
        generateDefaultFile = configuration.getBoolean(ConfigPath.GENERATE_DEFAULT_FILE.getPath());
        disableDoubleClickEvent = configuration.getBoolean(ConfigPath.DISABLE_DOUBLE_CLICK_EVENT.getPath());

        enableAntiDupe = configuration.getBoolean(ConfigPath.ENABLE_ANTI_DUPE.getPath());
        enableAntiDupeDiscordNotification = configuration.getBoolean(ConfigPath.ENABLE_ANTI_DUPE_DISCORD_NOTIFICATION.getPath());
        antiDupeDiscordWebhookUrl = configuration.getString(ConfigPath.ANTI_DUPE_DISCORD_WEBHOOK_URL.getPath());
        antiDupeMessage = configuration.getString(ConfigPath.ANTI_DUPE_MESSAGE.getPath());

        allClicksType = configuration.getStringList(ConfigPath.ALL_CLICKS_TYPE.getPath()).stream()
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

        enableCacheItemStack = configuration.getBoolean(ConfigPath.ENABLE_CACHE_ITEM_STACK.getPath());
        enableCooldownClick = configuration.getBoolean(ConfigPath.ENABLE_COOLDOWN_CLICK.getPath());
        cooldownClickMilliseconds = configuration.getLong(ConfigPath.COOLDOWN_CLICK_MILLISECONDS.getPath());

        cachePlaceholderAPI = configuration.getLong(ConfigPath.CACHE_PLACEHOLDER_API.getPath());
        enableCachePlaceholderAPI = configuration.getBoolean(ConfigPath.ENABLE_CACHE_PLACEHOLDER_API.getPath());

        enableDownloadCommand = configuration.getBoolean(ConfigPath.ENABLE_DOWNLOAD_COMMAND.getPath());
        enablePlayerOpenInventoryLogs = configuration.getBoolean(ConfigPath.ENABLE_PLAYER_OPEN_INVENTORY_LOGS.getPath());
    }

    public void save(FileConfiguration configuration, File file) {
        if (!updated) {
            return;
        }
        configuration.set(ConfigPath.ENABLE_DEBUG.getPath(), enableDebug);
        configuration.set(ConfigPath.ENABLE_DEBUG_TIME.getPath(), enableDebugTime);
        configuration.set(ConfigPath.ENABLE_INFORMATION_MESSAGE.getPath(), enableInformationMessage);
        configuration.set(ConfigPath.ENABLE_LOG_STORAGE_FILE.getPath(), enableLogStorageFile);
        configuration.set(ConfigPath.SKIP_UPDATE_CHECK.getPath(), skipUpdateCheck);
        configuration.set(ConfigPath.ENABLE_OPEN_MESSAGE.getPath(), enableOpenMessage);
        configuration.set(ConfigPath.ENABLE_MINI_MESSAGE_FORMAT.getPath(), enableMiniMessageFormat);
        configuration.set(ConfigPath.ENABLE_PLAYER_COMMAND_IN_CHAT.getPath(), enablePlayerCommandInChat);
        configuration.set(ConfigPath.ENABLE_FAST_EVENT.getPath(), enableFastEvent);

        configuration.set(ConfigPath.SECONDS_SAVE_PLAYER_DATA.getPath(), secondsSavePlayerData);
        configuration.set(ConfigPath.SECONDS_SAVE_PLAYER_INVENTORIES.getPath(), secondsSavePlayerInventories);

        configuration.set(ConfigPath.MAIN_MENU.getPath(), mainMenu);
        configuration.set(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU.getPath(), useSwapItemOffHandKeyToOpenMainMenu);
        configuration.set(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU_NEEDS_SHIFT.getPath(), useSwapItemOffHandKeyToOpenMainMenuNeedsShift);

        configuration.set(ConfigPath.SPECIFY_PATH_MENUS.getPath(), specifyPathMenus);
        configuration.set(ConfigPath.GENERATE_DEFAULT_FILE.getPath(), generateDefaultFile);
        configuration.set(ConfigPath.DISABLE_DOUBLE_CLICK_EVENT.getPath(), disableDoubleClickEvent);

        configuration.set(ConfigPath.ENABLE_ANTI_DUPE.getPath(), enableAntiDupe);
        configuration.set(ConfigPath.ENABLE_ANTI_DUPE_DISCORD_NOTIFICATION.getPath(), enableAntiDupeDiscordNotification);
        configuration.set(ConfigPath.ANTI_DUPE_DISCORD_WEBHOOK_URL.getPath(), antiDupeDiscordWebhookUrl);
        configuration.set(ConfigPath.ANTI_DUPE_MESSAGE.getPath(), antiDupeMessage);

        List<String> clickTypeNames = allClicksType.stream()
                .map(Enum::name)
                .collect(Collectors.toList());
        configuration.set(ConfigPath.ALL_CLICKS_TYPE.getPath(), clickTypeNames);

        configuration.set(ConfigPath.ENABLE_CACHE_ITEM_STACK.getPath(), enableCacheItemStack);
        configuration.set(ConfigPath.ENABLE_COOLDOWN_CLICK.getPath(), enableCooldownClick);
        configuration.set(ConfigPath.COOLDOWN_CLICK_MILLISECONDS.getPath(), cooldownClickMilliseconds);
        configuration.set(ConfigPath.CACHE_PLACEHOLDER_API.getPath(), cachePlaceholderAPI);
        configuration.set(ConfigPath.ENABLE_CACHE_PLACEHOLDER_API.getPath(), enableCachePlaceholderAPI);
        configuration.set(ConfigPath.ENABLE_DOWNLOAD_COMMAND.getPath(), enableDownloadCommand);
        configuration.set(ConfigPath.ENABLE_PLAYER_OPEN_INVENTORY_LOGS.getPath(), enablePlayerOpenInventoryLogs);
        updated = false;
        try {
            configuration.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().warning("[zMenu] Unable to save config file: " + e.getMessage());
        }
    }

    private enum ConfigPath {
        ENABLE_DEBUG("enable-debug"),
        ENABLE_DEBUG_TIME("enable-debug-time"),
        ENABLE_INFORMATION_MESSAGE("enable-information-message"),
        ENABLE_LOG_STORAGE_FILE("enable-log-storage-file"),
        SKIP_UPDATE_CHECK("skip-update-check"),
        ENABLE_OPEN_MESSAGE("enable-open-message"),
        ENABLE_MINI_MESSAGE_FORMAT("enable-mini-message-format"),
        ENABLE_PLAYER_COMMAND_IN_CHAT("enable-player-command-in-chat"),
        ENABLE_FAST_EVENT("enable-fast-event"),

        SECONDS_SAVE_PLAYER_DATA("seconds-save-player-data"),
        SECONDS_SAVE_PLAYER_INVENTORIES("seconds-save-player-inventories"),

        MAIN_MENU("main-menu"),
        USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU("use-swap-item-off-hand-key-to-open-main-menu"),
        USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU_NEEDS_SHIFT("use-swap-item-off-hand-key-to-open-main-menu-needs-shift"),

        SPECIFY_PATH_MENUS("specify-path-menus"),
        GENERATE_DEFAULT_FILE("generate-default-file"),
        DISABLE_DOUBLE_CLICK_EVENT("disable-double-click-event"),

        ENABLE_ANTI_DUPE("enable-anti-dupe"),
        ENABLE_ANTI_DUPE_DISCORD_NOTIFICATION("enable-anti-dupe-discord-notification"),
        ANTI_DUPE_DISCORD_WEBHOOK_URL("anti-dupe-discord-webhook-url"),
        ANTI_DUPE_MESSAGE("anti-dupe-message"),

        ALL_CLICKS_TYPE("all-clicks-type"),

        ENABLE_CACHE_ITEM_STACK("enable-cache-item-stack"),
        ENABLE_COOLDOWN_CLICK("enable-cooldown-click"),
        COOLDOWN_CLICK_MILLISECONDS("cooldown-click-milliseconds"),

        CACHE_PLACEHOLDER_API("cache-placeholder-api"),
        ENABLE_CACHE_PLACEHOLDER_API("enable-cache-placeholder-api"),

        ENABLE_DOWNLOAD_COMMAND("enable-download-command"),
        ENABLE_PLAYER_OPEN_INVENTORY_LOGS("enable-player-open-inventory-logs")
        ;
        private final String path;

        ConfigPath(String path) {
            this.path = path;
        }

        public String getPath() {
            return path;
        }
    }
}
