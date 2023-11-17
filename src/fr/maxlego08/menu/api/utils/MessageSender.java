package fr.maxlego08.menu.api.utils;

import org.bukkit.command.CommandSender;

/**
 * Sends messages to a {@link org.bukkit.command.CommandSender}.
 */
public interface MessageSender {

    /**
     * Sends a message to the specified {@link CommandSender}.
     *
     * @param sender  The CommandSender to whom the message will be sent.
     * @param message The message to be sent.
     */
    void sendMessage(CommandSender sender, String message);

}
