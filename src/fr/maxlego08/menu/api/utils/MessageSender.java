package fr.maxlego08.menu.api.utils;

import org.bukkit.command.CommandSender;

/**
 * Sends a message to a {@link org.bukkit.entity.Player}
 */
public interface MessageSender {

    /**
     * Send message to user
     *
     * @param sender  - User
     * @param message - Message
     */
    void sendMessage(CommandSender sender, String message);

}
