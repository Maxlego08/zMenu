package fr.maxlego08.menu.api.configuration;

import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.configuration.annotation.ConfigUpdate;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.api.utils.OpGrantMethod;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.inventory.ClickType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Configuration {

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

    @ConfigOption(
        key = "enablePlayerCommandsAsOPAction",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable player commands as OP action (requires server restart)"
    )
    public static boolean enablePlayerCommandsAsOPAction = false;

    public static OpGrantMethod opGrantMethod = OpGrantMethod.ATTACHMENT;

    @ConfigOption(
        key = "enableToast",
        type = DialogInputType.BOOLEAN,
        trueText = "<green>Enabled",
        falseText = "<red>Disabled",
        label = "Enable toast"
    )
    public static boolean enableToast = true;

    @ConfigUpdate
    public static boolean updated = false;
    /**
     * static Singleton instance.
     */
    private static volatile Configuration instance;

    /**
     * Private constructor for singleton.
     */
    private Configuration() {
    }

    /**
     * Return a singleton instance of Configuration.
     */
    public static Configuration getInstance() {
        // Double lock for thread safety.
        if (instance == null) {
            synchronized (Configuration.class) {
                if (instance == null) {
                    instance = new Configuration();
                }
            }
        }
        return instance;
    }

    public void load(@NotNull FileConfiguration fileConfiguration) {

        enableDebug = fileConfiguration.getBoolean(ConfigPath.ENABLE_DEBUG.getPath());
        enableDebugTime = fileConfiguration.getBoolean(ConfigPath.ENABLE_DEBUG_TIME.getPath());
        enableInformationMessage = fileConfiguration.getBoolean(ConfigPath.ENABLE_INFORMATION_MESSAGE.getPath());
        enableLogStorageFile = fileConfiguration.getBoolean(ConfigPath.ENABLE_LOG_STORAGE_FILE.getPath());
        skipUpdateCheck = fileConfiguration.getBoolean(ConfigPath.SKIP_UPDATE_CHECK.getPath());
        enableOpenMessage = fileConfiguration.getBoolean(ConfigPath.ENABLE_OPEN_MESSAGE.getPath());
        enableMiniMessageFormat = fileConfiguration.getBoolean(ConfigPath.ENABLE_MINI_MESSAGE_FORMAT.getPath());
        enablePlayerCommandInChat = fileConfiguration.getBoolean(ConfigPath.ENABLE_PLAYER_COMMAND_IN_CHAT.getPath());
        enableFastEvent = fileConfiguration.getBoolean(ConfigPath.ENABLE_FAST_EVENT.getPath());

        secondsSavePlayerData = fileConfiguration.getInt(ConfigPath.SECONDS_SAVE_PLAYER_DATA.getPath());
        secondsSavePlayerInventories = fileConfiguration.getInt(ConfigPath.SECONDS_SAVE_PLAYER_INVENTORIES.getPath());

        mainMenu = fileConfiguration.getString(ConfigPath.MAIN_MENU.getPath());
        useSwapItemOffHandKeyToOpenMainMenu = fileConfiguration.getBoolean(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU.getPath());
        useSwapItemOffHandKeyToOpenMainMenuNeedsShift = fileConfiguration.getBoolean(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU_NEEDS_SHIFT.getPath());

        specifyPathMenus = fileConfiguration.getStringList(ConfigPath.SPECIFY_PATH_MENUS.getPath());
        generateDefaultFile = fileConfiguration.getBoolean(ConfigPath.GENERATE_DEFAULT_FILE.getPath());
        disableDoubleClickEvent = fileConfiguration.getBoolean(ConfigPath.DISABLE_DOUBLE_CLICK_EVENT.getPath());

        enableAntiDupe = fileConfiguration.getBoolean(ConfigPath.ENABLE_ANTI_DUPE.getPath());
        enableAntiDupeDiscordNotification = fileConfiguration.getBoolean(ConfigPath.ENABLE_ANTI_DUPE_DISCORD_NOTIFICATION.getPath());
        antiDupeDiscordWebhookUrl = fileConfiguration.getString(ConfigPath.ANTI_DUPE_DISCORD_WEBHOOK_URL.getPath());
        antiDupeMessage = fileConfiguration.getString(ConfigPath.ANTI_DUPE_MESSAGE.getPath());

        List<String> clickTypeStrings = fileConfiguration.getStringList(ConfigPath.ALL_CLICKS_TYPE.getPath());
        List<ClickType> clickTypes = new ArrayList<>(clickTypeStrings.size());
        for (String name : clickTypeStrings) {
            try {
                clickTypes.add(ClickType.valueOf(name));
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("[zMenu] Invalid click type in config: " + name);
            }
        }
        allClicksType = clickTypes;

        enableCacheItemStack = fileConfiguration.getBoolean(ConfigPath.ENABLE_CACHE_ITEM_STACK.getPath());
        enableCooldownClick = fileConfiguration.getBoolean(ConfigPath.ENABLE_COOLDOWN_CLICK.getPath());
        cooldownClickMilliseconds = fileConfiguration.getLong(ConfigPath.COOLDOWN_CLICK_MILLISECONDS.getPath());

        cachePlaceholderAPI = fileConfiguration.getLong(ConfigPath.CACHE_PLACEHOLDER_API.getPath());
        enableCachePlaceholderAPI = fileConfiguration.getBoolean(ConfigPath.ENABLE_CACHE_PLACEHOLDER_API.getPath());

        enableDownloadCommand = fileConfiguration.getBoolean(ConfigPath.ENABLE_DOWNLOAD_COMMAND.getPath());
        enablePlayerOpenInventoryLogs = fileConfiguration.getBoolean(ConfigPath.ENABLE_PLAYER_OPEN_INVENTORY_LOGS.getPath());

        enablePlayerCommandsAsOPAction = fileConfiguration.getBoolean(ConfigPath.ENABLE_PLAYER_COMMANDS_AS_OP_ACTION.getPath());
        try {
            opGrantMethod = OpGrantMethod.valueOf(fileConfiguration.getString(ConfigPath.OP_GRANT_METHOD.getPath(), OpGrantMethod.ATTACHMENT.name()).toUpperCase());
        } catch (IllegalArgumentException e) {
            Logger.info("Invalid op grant method in config, defaulting to ATTACHMENT.");
            opGrantMethod = OpGrantMethod.ATTACHMENT;
        }
        enableToast = fileConfiguration.getBoolean(ConfigPath.ENABLE_TOAST.getPath(), true);
    }

    public void save(@NotNull FileConfiguration fileConfiguration,@NotNull File file) {
        if (!updated) {
            return;
        }
        fileConfiguration.set(ConfigPath.ENABLE_DEBUG.getPath(), enableDebug);
        fileConfiguration.set(ConfigPath.ENABLE_DEBUG_TIME.getPath(), enableDebugTime);
        fileConfiguration.set(ConfigPath.ENABLE_INFORMATION_MESSAGE.getPath(), enableInformationMessage);
        fileConfiguration.set(ConfigPath.ENABLE_LOG_STORAGE_FILE.getPath(), enableLogStorageFile);
        fileConfiguration.set(ConfigPath.SKIP_UPDATE_CHECK.getPath(), skipUpdateCheck);
        fileConfiguration.set(ConfigPath.ENABLE_OPEN_MESSAGE.getPath(), enableOpenMessage);
        fileConfiguration.set(ConfigPath.ENABLE_MINI_MESSAGE_FORMAT.getPath(), enableMiniMessageFormat);
        fileConfiguration.set(ConfigPath.ENABLE_PLAYER_COMMAND_IN_CHAT.getPath(), enablePlayerCommandInChat);
        fileConfiguration.set(ConfigPath.ENABLE_FAST_EVENT.getPath(), enableFastEvent);

        fileConfiguration.set(ConfigPath.SECONDS_SAVE_PLAYER_DATA.getPath(), secondsSavePlayerData);
        fileConfiguration.set(ConfigPath.SECONDS_SAVE_PLAYER_INVENTORIES.getPath(), secondsSavePlayerInventories);

        fileConfiguration.set(ConfigPath.MAIN_MENU.getPath(), mainMenu);
        fileConfiguration.set(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU.getPath(), useSwapItemOffHandKeyToOpenMainMenu);
        fileConfiguration.set(ConfigPath.USE_SWAP_ITEM_OFF_HAND_KEY_TO_OPEN_MAIN_MENU_NEEDS_SHIFT.getPath(), useSwapItemOffHandKeyToOpenMainMenuNeedsShift);

        fileConfiguration.set(ConfigPath.SPECIFY_PATH_MENUS.getPath(), specifyPathMenus);
        fileConfiguration.set(ConfigPath.GENERATE_DEFAULT_FILE.getPath(), generateDefaultFile);
        fileConfiguration.set(ConfigPath.DISABLE_DOUBLE_CLICK_EVENT.getPath(), disableDoubleClickEvent);

        fileConfiguration.set(ConfigPath.ENABLE_ANTI_DUPE.getPath(), enableAntiDupe);
        fileConfiguration.set(ConfigPath.ENABLE_ANTI_DUPE_DISCORD_NOTIFICATION.getPath(), enableAntiDupeDiscordNotification);
        fileConfiguration.set(ConfigPath.ANTI_DUPE_DISCORD_WEBHOOK_URL.getPath(), antiDupeDiscordWebhookUrl);
        fileConfiguration.set(ConfigPath.ANTI_DUPE_MESSAGE.getPath(), antiDupeMessage);

        List<String> clickTypeNames = new ArrayList<>(allClicksType.size());
        for (ClickType clickType : allClicksType) {
            clickTypeNames.add(clickType.name());
        }
        fileConfiguration.set(ConfigPath.ALL_CLICKS_TYPE.getPath(), clickTypeNames);

        fileConfiguration.set(ConfigPath.ENABLE_CACHE_ITEM_STACK.getPath(), enableCacheItemStack);
        fileConfiguration.set(ConfigPath.ENABLE_COOLDOWN_CLICK.getPath(), enableCooldownClick);
        fileConfiguration.set(ConfigPath.COOLDOWN_CLICK_MILLISECONDS.getPath(), cooldownClickMilliseconds);
        fileConfiguration.set(ConfigPath.CACHE_PLACEHOLDER_API.getPath(), cachePlaceholderAPI);
        fileConfiguration.set(ConfigPath.ENABLE_CACHE_PLACEHOLDER_API.getPath(), enableCachePlaceholderAPI);
        fileConfiguration.set(ConfigPath.ENABLE_DOWNLOAD_COMMAND.getPath(), enableDownloadCommand);
        fileConfiguration.set(ConfigPath.ENABLE_PLAYER_OPEN_INVENTORY_LOGS.getPath(), enablePlayerOpenInventoryLogs);
        fileConfiguration.set(ConfigPath.ENABLE_PLAYER_COMMANDS_AS_OP_ACTION.getPath(), enablePlayerCommandsAsOPAction);
        fileConfiguration.set(ConfigPath.OP_GRANT_METHOD.getPath(), opGrantMethod.name());
        fileConfiguration.set(ConfigPath.ENABLE_TOAST.getPath(), enableToast);
        updated = false;
        try {
            fileConfiguration.save(file);
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
        ENABLE_PLAYER_OPEN_INVENTORY_LOGS("enable-player-open-inventory-logs"),

        ENABLE_PLAYER_COMMANDS_AS_OP_ACTION("enable-player-commands-as-op-action"),
        OP_GRANT_METHOD("op-grant-method"),
        ENABLE_TOAST("enable-toast");
      
        private final String path;

        ConfigPath(@NotNull String path) {
            this.path = path;
        }

        @Contract(pure = true)
        @NotNull
        public String getPath() {
            return this.path;
        }
    }
}
