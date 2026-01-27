package fr.maxlego08.menu.api.utils;

import net.md_5.bungee.api.chat.ClickEvent.Action;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * <p>Sends a message and opens a link.</p>
 * <p>For servers that are in 1.17+ it is advisable to use the <a href="https://docs.advntr.dev/minimessage/index.html">MiniMessage</a> format</p>
 */
public interface OpenLink {

    /**
     * Returns the action that will be performed on the click
     *
     * @return action
     */
    @Nullable
    Action getAction();

    /**
     * Returns the message that will be displayed
     *
     * @return message
     */
    @Nullable
    String getMessage();

    /**
     * Returns the value to be used for the click
     *
     * @return string
     */
    @Nullable
    String getLink();

    /**
     * Returns the value that will be replaced
     *
     * @return replace
     */
    @Nullable
    String getReplace();

    /**
     * @return list of string
     */
    @Nullable
    List<String> getHover();

    /**
     * Allows you to send messages to a player
     *
     * @param player The players
     * @param strings list of messages
     */
    void send(@NotNull Player player,@NotNull List<String> strings);

    /**
     * Allows to know if the object is valid
     *
     * @return boolean
     */
    boolean isValid();

}
