package fr.maxlego08.menu.api.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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

    /**
     * Sends a title and subtitle to the player with specified timings for the display. This method provides a way to
     * display prominent messages directly in the player's view, offering an engaging way to present information, alerts,
     * or narrative elements within the game. The timing parameters control the duration of the fade-in, the time the title
     * stays on the screen, and the fade-out duration, allowing for a customizable viewing experience.
     *
     * @param player   The player
     * @param title    The main title text to be displayed at the top of the screen. Can be formatted with color codes.
     * @param subtitle The secondary text to be displayed beneath the main title. Also supports formatting with color codes.
     * @param start    The duration in ticks (1 tick = 1/20th of a second) before the title and subtitle start fading in.
     * @param duration The time in ticks that the title and subtitle will stay fully visible on the screen before starting to fade out.
     * @param end      The duration in ticks for the title and subtitle to fade out.
     */
    void sendTitle(Player player, String title, String subtitle, long start, long duration, long end);

}
