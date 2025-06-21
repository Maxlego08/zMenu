package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.enums.MessageType;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum Message implements IMessage {

    PREFIX("&8(&6zMenu&8) "),
    AND("and"),

    VINVENTORY_ERROR("&cUnable to open inventory, internal error occurred. &8(&7Inventory ID: %id%&8)"),

    TIME_YEAR("%02d %year% %02d %month% %02d %day% %02d %hour% %02d %minute% %02d %second%"),
    TIME_MONTH("%02d %month% %02d %day% %02d %hour% %02d %minute% %02d %second%"),
    TIME_DAY("%02d %day% %02d %hour% %02d %minute% %02d %second%"),
    TIME_HOUR("%02d %hour% %02d minute(s) %02d %second%"),
    TIME_HOUR_SIMPLE("%02d:%02d:%02d"),
    TIME_MINUTE("%02d %minute% %02d %second%"),
    TIME_SECOND("%02d %second%"),

    FORMAT_SECOND("second"),
    FORMAT_SECONDS("seconds"),

    FORMAT_MINUTE("minute"),
    FORMAT_MINUTES("minutes"),

    FORMAT_HOUR("hour"),
    FORMAT_HOURS("hours"),

    FORMAT_DAY("d"),
    FORMAT_DAYS("days"),

    FORMAT_MONTH("m"),
    FORMAT_MONTHS("months"),

    FORMAT_YEAR("y"),
    FORMAT_YEARS("years"),

    COMMAND_SYNTAX_ERROR("&cYou must execute the command like this&7: &a%syntax%"),
    COMMAND_NO_PERMISSION("&cYou do not have permission to run this command."),
    COMMAND_NO_CONSOLE("&cOnly one player can execute this command."),
    COMMAND_NO_ARG("&cImpossible to find the command with its arguments."),
    COMMAND_SYNTAX_HELP("&f%syntax% &7» &7%description%"),
    DOCUMENTATION_INFORMATION("&7Documentation&8: &fhttps://docs.zmenu.dev/"),
    DOCUMENTATION_INFORMATION_LINK("&7Documentation&8: &f%link%"),

    INVENTORY_NOT_FOUND("&cUnable to find the &f%toName% &cinventory in the &f%name%&c inventory."),
    INVENTORY_OPEN_OTHER("&aYou have just opened the inventory &f%name%&a to the &3%player%&a."),
    INVENTORY_OPEN_SUCCESS("&aYou have just opened the inventory &f%name%&a."),
    INVENTORY_OPEN_ERROR_INVENTORY("&cImpossible to find the inventory &f%name%&c."),
    INVENTORY_OPEN_ERROR_COMMAND("&cImpossible to find the command &f%name%&c."),
    INVENTORY_OPEN_ERROR_PLAYER("&cUnable to find the player, please specify."),
    INVENTORY_OPEN_ERROR_CONSOLE("&cOnly one player can open an inventory."),
    INVENTORY_OPEN_ITEM_ERROR("&cInventory &f%name%&c doesn't have open item."),
    INVENTORY_OPEN_ITEM_SUCCESS("&aYou have just given the open item to the player &f%name%&a."),

    DESCRIPTION_OPEN("Allows you to open an inventory"),
    DESCRIPTION_SAVE("Allows you to save the item in your hand"),
    DESCRIPTION_RELOAD("Allows you to reload configuration files"),
    DESCRIPTION_VERSION("Show plugin version"),
    DESCRIPTION_LIST("Inventory list"),
    DESCRIPTION_TEST_DUPE("Test dupe"),
    DESCRIPTION_OPEN_ITEM("Give open item"),
    DESCRIPTION_DOWNLOAD("Download an inventory from a link (a discord link for example)"),
    DESCRIPTION_LOGIN("Login to the website"),
    DESCRIPTION_MARKETPLACE("Open marketplace inventory"),
    DESCRIPTION_DISCONNECT("WIP"),
    DESCRIPTION_CONVERT("Convert other configurations to zmenu"),
    DESCRIPTION_EDITOR("Open zmenu online editor"),
    DESCRIPTION_DOCUMENTATION("Open documentation"),
    DESCRIPTION_PLAYERS("Displays the list of commands for the players' data"),
    DESCRIPTION_PLAYERS_SET("Set new player data. You must set the expiration time in seconds. Put 0 to have no expiration"),
    DESCRIPTION_PLAYERS_ADD("Add a number to a value, works only for numbers."),
    DESCRIPTION_PLAYERS_SUBTRACT("Subtract a number to a value, works only for numbers."),
    DESCRIPTION_PLAYERS_REMOVE("Remove player data"),
    DESCRIPTION_PLAYERS_REMOVE_ALL("Remove all player data from a key"),
    DESCRIPTION_PLAYERS_GET("Get player data"),
    DESCRIPTION_PLAYERS_KEYS("Returns the list of keys of a player"),
    DESCRIPTION_PLAYERS_CLEAR_ALL("Clear all player's data"),
    DESCRIPTION_PLAYERS_CONVERT("Convert old players data"),
    DESCRIPTION_PLAYERS_CLEAR_PLAYER("Clear player's data"),
    DESCRIPTION_OPEN_MAIN_MENU("Open the main menu"),
    DESCRIPTION_CREATE("Create a new config file"),
    DESCRIPTION_INVENTORIES("Open inventories builder"),

    RELOAD("&aYou have just reloaded the configuration files. &8(&7%inventories% inventories&8)"),
    RELOAD_INVENTORY("&aYou have just reloaded the inventories files. &8(&7%inventories% inventories&8)"),
    RELOAD_INVENTORY_FILE("&aVous have just reloaded the inventory &f%name%&a."),
    RELOAD_COMMAND("&aYou have just reloaded the commands files."),
    RELOAD_COMMAND_FILE("&aVous have just reloaded the command &f%name%&a."),
    RELOAD_COMMAND_ERROR("&cIt is not possible to reload the command &f%name%&c."),
    RELOAD_FILES("&aYou have just reloaded config.json and messages.yml files."),

    PLAYERS_DATA_CLEAR_ALL("&aYou have just deleted the datas of all the players."),
    PLAYERS_DATA_CLEAR_PLAYER("&aYou have just deleted the player's data &f%player%&a."),

    PLAYERS_DATA_SET("&aYou have just added a data for the &b%player% &a with the &f%key%&a."),
    PLAYERS_DATA_ADD("&aYou have just added a data for the &b%player% &a with the &f%key%&a."),
    PLAYERS_DATA_SUBTRACT("&aYou have just subtract a data for the &b%player% &a with the &f%key%&a."),
    PLAYERS_DATA_KEYS_SUCCESS("&aPlayer's Key &f%player%&8: &7%keys%"),
    PLAYERS_DATA_KEYS_EMPTY("&cThe &f%player% &chas no key."),
    PLAYERS_DATA_GET_SUCCESS(
            "&fKey&8: &7%key%",
            "&fExpired at (timestamp)&8: &7%expiredAt%",
            "&fValue&8: &7%value%"
    ),
    PLAYERS_DATA_GET_ERROR("&cCannot find the key &f%key%&c."),
    PLAYERS_DATA_REMOVE_SUCCESS("&aYou have just deleted the key &f%key% &ffor &b%player%&a."),
    PLAYERS_DATA_REMOVE_ALL_SUCCESS("&aYou have just deleted all key's &b%key%&a."),
    PLAYERS_DATA_REMOVE_ERROR("&cCannot find the key &f%key%&c."),
    PLAYERS_DATA_CONVERT_SUCCESS("&aYou have just converted the datas&a."),
    PLAYERS_DATA_CONVERT_CONFIRM("&cAre you sure you want to convert the datas ? Re-run the command to confirm."),

    WEBSITE_LOGIN_ERROR_TOKEN("&cYour token seems invalid, please try again."),
    WEBSITE_LOGIN_ERROR_ALREADY("&cYou are already connected to the site."),
    WEBSITE_LOGIN_ERROR_INFO("&cAn error occurred during your connection, please try again."),
    WEBSITE_LOGIN_PROCESS("&7Connection in progress, please wait."),
    WEBSITE_LOGIN_SUCCESS("&aYou have successfully connected to the site.", "&aYou can now access your purchased resources and the inventory editor."),
    WEBSITE_NOT_CONNECT("&cYou need to log into the site before you can do that."),
    WEBSITE_ALREADY_INVENTORY("&cYou are already performing this action, please wait."),
    WEBSITE_MARKETPLACE_WAIT("&7Download resources, please wait before opening inventory."),
    WEBSITE_INVENTORY_WAIT("&7Download inventory &f%name%&7, please wait before opening inventory."),
    WEBSITE_INVENTORY_EXIST("&cThe inventory already exists. Unable to download."),
    WEBSITE_INVENTORY_SUCCESS("&aInventory &f%name%&a download successfully. &8(&7use /zm reload to load this inventory&8)"),
    WEBSITE_INVENTORY_ERROR("&cAn error occurred while downloading the file."),
    WEBSITE_MARKETPLACE_ERROR("&cUnable to retrieve data from the site, please try again."),
    WEBSITE_DISCONNECT_SUCCESS("&cYou have just deleted the link to the site."),
    WEBSITE_DISCONNECT_ERROR("&cYou are not connected to the site."),
    WEBSITE_DOWNLOAD_ERROR_TYPE("&cThe link is not a yml file."),
    WEBSITE_DOWNLOAD_ERROR_NAME("&cCannot find file name."),
    WEBSITE_DOWNLOAD_ERROR_CONSOLE("&cAn error has occurred, look at the console."),
    WEBSITE_DOWNLOAD_START("&7Start downloading inventory, please wait."),

    PLACEHOLDER_NEVER("never"),

    LIST_EMPTY("&cNo inventory of available."),
    LIST_INFO("&fInventories of &a%plugin% &8(&7%amount%&8): &7%inventories%"),

    INVENTORY_CREATE_ERROR_SIZE("&cThe inventory size should be included in 9 and 54."),
    INVENTORY_CREATE_ERROR_ALREADY("&cThe file &f%name%&c already exist."),
    INVENTORY_CREATE_ERROR_EXCEPTION("&cAn error has occurred&8: &f%error%"),
    INVENTORY_CREATE_SUCCESS("&aYou have just created the inventory &f%name%&a."),

    SAVE_ERROR_EMPTY("&cYou must have an item in hand to save this item."),
    SAVE_ERROR_NAME("&cThe name already exists for this item, please select another one."),
    SAVE_ERROR_TYPE("&cCannot find save type."),
    SAVE_SUCCESS("&aYou just saved the item &f%name%&a."),
    CLICK_COOLDOWN(MessageType.ACTION, "&cPlease wait a little between two clicks."),

    COMMAND_ARGUMENT_INTEGER("&cThe argument &f%argument%&c must be an integer."),
    COMMAND_ARGUMENT_STRING("&cThe argument &f%argument%&c must be a string."),
    COMMAND_ARGUMENT_BOOLEAN("&cThe argument &f%argument%&c must be a boolean."),
    COMMAND_ARGUMENT_DOUBLE("&cThe argument &f%argument%&c must be a double."),
    COMMAND_ARGUMENT_ONLINE_PLAYER("&cThe argument &f%argument%&c must be a player."),
    COMMAND_ARGUMENT_PLAYER("&cThe argument &f%argument%&c must be a player."),
    COMMAND_ARGUMENT_ENTITY("&cThe argument &f%argument%&c must be an entity."),
    COMMAND_ARGUMENT_LOCATION("&cThe argument &f%argument%&c must be a location."),
    COMMAND_ARGUMENT_MATERIAL("&cThe argument &f%argument%&c must be a material."),
    COMMAND_ARGUMENT_BLOCK("&cThe argument &f%argument%&c must be a block."),
    COMMAND_ARGUMENT_WORLD("&cThe argument &f%argument%&c must be a world.");

    private List<String> messages;
    private String message;
    private MessageType type = MessageType.TCHAT;

    private ItemStack itemStack;

    Message(String message) {
        this.message = message;
    }

    Message(String... message) {
        this.messages = Arrays.asList(message);
    }

    Message(MessageType type, String... message) {
        this.messages = Arrays.asList(message);
        this.type = type;
    }

    Message(MessageType type, String message) {
        this.message = message;
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public String toMsg() {
        return message;
    }

    public String msg() {
        return message;
    }

    public List<String> getMessages() {
        return messages == null ? Collections.singletonList(message) : messages;
    }

    public void setMessages(List<String> messages) {
        this.messages = messages;
    }

    public boolean isMessage() {
        return messages != null && messages.size() > 1;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String replace(String a, String b) {
        return message.replace(a, b);
    }

    public MessageType getType() {
        return this.type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

}

