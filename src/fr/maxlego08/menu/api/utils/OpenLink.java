package fr.maxlego08.menu.api.utils;

import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.entity.Player;

import java.util.List;

public interface OpenLink {

    /**
     * Returns the action that will be performed on the click
     *
     * @return action
     */
    Action getAction();

    /**
     * Returns the message that will be displayed
     *
     * @return message
     */
    String getMessage();

    /**
     * Returns the value to be used for the click
     *
     * @return string
     */
    String getLink();

    /**
     * Returns the value that will be replaced
     *
     * @return replace
     */
    String getReplace();

    /**
     * @return
     */
    List<String> getHover();

    /**
     * Allows you to send messages to a player
     *
     * @param player
     * @param strings
     */
    void send(Player player, List<String> strings);

    /**
     * Allows to know if the object is valid
     *
     * @return boolean
     */
    boolean isValid();

}
