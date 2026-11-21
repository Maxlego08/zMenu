package fr.maxlego08.menu.common.utils;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.IMessage;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.Message;
import fr.robie.minimessageflow.Placeholder;
import net.kyori.adventure.text.Component;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Allows you to manage messages sent to players and the console
 *
 * @author Maxence
 */
public abstract class MessageUtils extends LocationUtils {

    /**
     * Allows you to send a message to a command sender without a prefix
     *
     * @param sender  User who sent the command
     * @param message The message - Using the Message enum for simplified message
     *                management
     * @param args    The arguments - The arguments work in pairs, you must put for
     *                example %test% and then the value
     */
    public static void messageWO(MenuPlugin plugin, CommandSender sender, IMessage message, Object... args) {
        if (!message.getMessages().isEmpty()) {
            message.getMessages().forEach(msg -> sender.sendMessage(format(sender, msg, args)));
        } else {
            sender.sendMessage(format(sender, message.getMessage(), args));
        }
    }

    /**
     * Allows you to send a message to a command sender without a prefix
     *
     * @param sender  User who sent the command
     * @param message The message - Using the Message enum for simplified message
     *                management
     * @param args    The arguments - The arguments work in pairs; you must put, for
     *                example, %test% and then the value
     */
    public static void messageWO(MenuPlugin plugin, CommandSender sender, String message, Object... args) {
        sender.sendMessage(format(sender, message, args));
    }

    /**
     * Allows you to send a message to a command sender
     *
     * @param sender  User who sent the command
     * @param message The message - Using the Message enum for simplified message
     *                management
     * @param args    The arguments - The arguments work in pairs, you must put for
     *                example %test% and then the value
     */
    public static void message(MenuPlugin plugin, CommandSender sender, String message, Object... args) {
        sender.sendMessage(format(sender, Message.PREFIX.msg() + message, args));
    }

    /**
     * Allows you to send a message to a command sender
     *
     * @param sender  User who sent the command
     * @param message The message - Using the Message enum for simplified message
     *                management
     * @param args    The arguments - The arguments work in pairs, you must put for
     *                example %test% and then the value
     */
    public static void message(MenuPlugin plugin, CommandSender sender, IMessage message, Object... args) {
        if (sender instanceof Player player) {
            switch (message.getType()) {
                case CENTER:
                    if (!message.getMessages().isEmpty()) {
                        message.getMessages().forEach(msg -> plugin.getMetaUpdater().sendMessage(sender, getCenteredMessage(getMessage(msg, args))));
                    } else {
                        plugin.getMetaUpdater().sendMessage(sender, getCenteredMessage(getMessage(message, args)));
                    }

                    break;
                case ACTION:
                    player.sendActionBar(format(sender, message.getMessage(), args));
                    break;
                case TCHAT:
                    if (!message.getMessages().isEmpty()) {
                        message.getMessages().forEach(msg -> sender.sendMessage(format(sender, Message.PREFIX.msg() + msg, args)));
                    } else {
                        sender.sendMessage(format(sender, Message.PREFIX.msg() + message.getMessage(), args));
                    }
                    break;
                default:
                    break;
            }
        } else {
            if (!message.getMessages().isEmpty()) {
                message.getMessages().forEach(msg -> sender.sendMessage(format(sender, Message.PREFIX.msg() + msg, args)));
            } else {
                sender.sendMessage(format(sender, Message.PREFIX.msg() + message.getMessage(), args));
            }
        }
    }

    /**
     * Formats a message as written, its arguments given as values: the text stays the same from one call to the next,
     * so it is compiled once. An argument named {@code %key%} is a value of the message (read as formatted text, as
     * before); any other is replaced in the text.
     *
     * @param sender  the viewer
     * @param message the message as written
     * @param args    the arguments, in pairs
     * @return the formatted message
     */
    public static @NotNull Component format(@NotNull CommandSender sender, @NotNull String message, Object... args) {
        if (args.length % 2 != 0) throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");
        if (args.length == 0) return TextService.get().flow().format(message, Placeholder.empty(), sender);
        Placeholder.Builder values = Placeholder.builder();
        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null) throw new IllegalArgumentException("Keys and replacement values must not be null.");
            String key = args[i].toString();
            String value = args[i + 1].toString();
            if (key.length() > 2 && key.charAt(0) == '%' && key.charAt(key.length() - 1) == '%' && key.indexOf('%', 1) == key.length() - 1) {
                values.registerFormatted(key.substring(1, key.length() - 1), value);
            } else {
                message = message.replace(key, value);
            }
        }
        return TextService.get().flow().format(message, values.build(), sender);
    }

    public static String getMessage(IMessage message, Object... args) {
        return getMessage(message.getMessage(), args);
    }

    public static String getMessage(String message, Object... args) {

        if (args.length % 2 != 0) throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");

        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null) throw new IllegalArgumentException("Keys and replacement values must not be null.");
            message = message.replace(args[i].toString(), args[i + 1].toString());
        }
        return message;
    }

    protected static String getCenteredMessage(String message) {
        if (message == null || message.isEmpty()) return "";
        message = ChatColor.translateAlternateColorCodes('&', message);

        int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;

        for (char c : message.toCharArray()) {
            if (c == '§') {
                previousCode = true;
            } else if (previousCode) {
                previousCode = false;
                isBold = c == 'l' || c == 'L';
            } else {
                DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                messagePxSize++;
            }
        }

        int halvedMessageSize = messagePxSize / 2;
        int CENTER_PX = 154;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while (compensated < toCompensate) {
            sb.append(" ");
            compensated += spaceLength;
        }
        return sb + message;
    }

}
