package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

/**
 * <p>Performs actions for a {@link Button}.</p>
 */
public interface PerformButton {

    /**
     * Returns the list of commands that the player will execute
     *
     * @return commands list
     */
    List<String> getCommands();

    /**
     * Returns the list of console that the player will execute
     *
     * @return console commands
     */
    List<String> getConsoleCommands();

    /**
     * Returns the list of commands that the console will execute if click is
     * right
     *
     * @return console commands
     */
    List<String> getConsoleRightCommands();

    /**
     * Returns the list of commands that the console will execute if click is
     * left
     *
     * @return console commands
     */
    List<String> getConsoleLeftCommands();

    /**
     * Returns the list of commands that the console will execute if player has
     * permission
     *
     * @return console commands
     */
    List<String> getConsolePermissionCommands();

    /**
     * Returns the permission the player must have to use the console permission
     *
     * @return permissions
     */
    String getConsolePermission();

    /**
     * Executes commands
     *
     * @param player Who execute the commands
     * @param type   The click type
     */
    void execute(Player player, ClickType type);

}
