package fr.maxlego08.menu.save;

import fr.maxlego08.menu.zcore.utils.storage.Persist;
import fr.maxlego08.menu.zcore.utils.storage.Savable;
import org.bukkit.event.inventory.ClickType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Config implements Savable {

    // Enable debug, allows you to display errors in the console that would normally be hidden.
    public static boolean enableDebug = false;

    // Enable debug time, allows you to display the code execution time in nanosecond, perfect for testing the effectiveness of the plugin.
    public static boolean enableDebugTime = false;

    // Enable information message, allows you to view messages that tell you about an inventory or that an order has been successfully loaded.
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
    public static boolean enableFastEvent = true;

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

    public void save(Persist persist) {
        persist.save(getInstance());
    }

    public void load(Persist persist) {
        persist.loadOrSaveDefault(getInstance(), Config.class);
    }

}
