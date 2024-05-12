package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.api.utils.IMessage;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import fr.maxlego08.menu.zcore.utils.players.ActionBar;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.Constructor;
import java.util.List;

/**
 * Allows you to manage messages sent to players and the console
 *
 * @author Maxence
 */
public abstract class MessageUtils extends LocationUtils {

    /**
     * Send message without prefix
     *
     * @param sender
     * @param message
     * @param args
     */
    protected void messageWO(CommandSender sender, IMessage message, Object... args) {
        Meta.meta.sendMessage(sender, getMessage(message, args));
    }

    /**
     * Send message without prefix
     *
     * @param sender
     * @param message
     * @param args
     */
    protected void messageWO(CommandSender sender, String message, Object... args) {
        Meta.meta.sendMessage(sender, getMessage(message, args));
    }

    /**
     * @param sender
     * @param message
     * @param args
     */
    protected void message(CommandSender sender, String message, Object... args) {
        Meta.meta.sendMessage(sender, Message.PREFIX.msg() + getMessage(message, args));
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
    protected void message(CommandSender sender, IMessage message, Object... args) {

        if (sender instanceof ConsoleCommandSender) {
            if (message.getMessages().size() > 0) {
                message.getMessages().forEach(msg -> Meta.meta.sendMessage(sender, Message.PREFIX.msg() + getMessage(msg, args)));
            } else {
                Meta.meta.sendMessage(sender, Message.PREFIX.msg() + getMessage(message, args));
            }
        } else {

            Player player = (Player) sender;
            switch (message.getType()) {
                case CENTER:
                    if (message.getMessages().size() > 0) {
                        message.getMessages()
                                .forEach(msg -> Meta.meta.sendMessage(sender, this.getCenteredMessage(getMessage(msg, args))));
                    } else {
                        Meta.meta.sendMessage(sender, this.getCenteredMessage(getMessage(message, args)));
                    }

                    break;
                case ACTION:
                    Meta.meta.sendAction(player, getMessage(message, args));
                    break;
                case TCHAT:
                    if (message.getMessages().size() > 0) {
                        message.getMessages()
                                .forEach(msg -> Meta.meta.sendMessage(sender, Message.PREFIX.msg() + getMessage(msg, args)));
                    } else {
                        Meta.meta.sendMessage(sender, Message.PREFIX.msg() + getMessage(message, args));
                    }
                    break;
                case TITLE:
                    // title message management
                    String title = message.getTitle();
                    String subTitle = message.getSubTitle();
                    int fadeInTime = message.getStart();
                    int showTime = message.getTime();
                    int fadeOutTime = message.getEnd();
                    this.title(player, this.getMessage(title, args), this.getMessage(subTitle, args), fadeInTime, showTime,
                            fadeOutTime);
                    break;
                default:
                    break;

            }

        }
    }

    /**
     * @param message
     * @param args
     */
    protected void broadcast(IMessage message, Object... args) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            message(player, message, args);
        }
        message(Bukkit.getConsoleSender(), message, args);
    }

    /**
     * @param player
     * @param message
     * @param args
     */
    protected void actionMessage(Player player, IMessage message, Object... args) {
        ActionBar.sendActionBar(player, getMessage(message, args));
    }

    protected String getMessage(IMessage message, Object... args) {
        return getMessage(message.getMessage(), args);
    }

    protected String getMessage(String message, Object... args) {

        if (args.length % 2 != 0)
            throw new IllegalArgumentException("Number of invalid arguments. Arguments must be in pairs.");

        for (int i = 0; i < args.length; i += 2) {
            if (args[i] == null || args[i + 1] == null)
                throw new IllegalArgumentException("Keys and replacement values must not be null.");
            message = message.replace(args[i].toString(), args[i + 1].toString());
        }
        return message;
    }

    protected final Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server."
                    + Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3] + "." + name);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Send title to player
     *
     * @param player
     * @param title
     * @param subtitle
     * @param fadeInTime
     * @param showTime
     * @param fadeOutTime
     */
    protected void title(Player player, String title, String subtitle, int fadeInTime, int showTime, int fadeOutTime) {

        if (NMSUtils.isNewVersion()) {
            player.sendTitle(title, subtitle, fadeInTime, showTime, fadeOutTime);
            return;
        }

        try {
            Object chatTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + title + "\"}");
            Constructor<?> titleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object packet = titleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("TITLE").get(null), chatTitle,
                    fadeInTime, showTime, fadeOutTime);

            Object chatsTitle = getNMSClass("IChatBaseComponent").getDeclaredClasses()[0].getMethod("a", String.class)
                    .invoke(null, "{\"text\": \"" + subtitle + "\"}");
            Constructor<?> timingTitleConstructor = getNMSClass("PacketPlayOutTitle").getConstructor(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0], getNMSClass("IChatBaseComponent"),
                    int.class, int.class, int.class);
            Object timingPacket = timingTitleConstructor.newInstance(
                    getNMSClass("PacketPlayOutTitle").getDeclaredClasses()[0].getField("SUBTITLE").get(null),
                    chatsTitle, fadeInTime, showTime, fadeOutTime);

            sendPacket(player, packet);
            sendPacket(player, timingPacket);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param player
     * @param packet
     */
    protected final void sendPacket(Player player, Object packet) {
        try {
            Object handle = player.getClass().getMethod("getHandle").invoke(player);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param message
     * @return message
     */
    protected String getCenteredMessage(String message) {
        if (message == null || message.equals(""))
            return "";
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

    protected void broadcastCenterMessage(List<String> messages) {
        messages.stream().map(e -> e = getCenteredMessage(e)).forEach(e -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                messageWO(player, e);
            }
        });
    }

    protected void broadcastAction(String message) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            ActionBar.sendActionBar(player, papi(message, player));
        }
    }

}
