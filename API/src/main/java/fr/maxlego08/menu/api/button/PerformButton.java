package fr.maxlego08.menu.api.button;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

/**
 * The PerformButton interface performs actions for a {@link Button}.
 */
public interface PerformButton {

    /**
     * Returns the list of commands that the player will execute.
     *
     * @return The list of commands to be executed.
     */
    List<String> getCommands();

    /**
     * Returns the list of console commands that will be executed.
     *
     * @return The list of console commands to be executed.
     */
    List<String> getConsoleCommands();

    /**
     * Returns the list of console commands that will be executed if the click is a right-click.
     *
     * @return The list of console commands to be executed on right-click.
     */
    List<String> getConsoleRightCommands();

    /**
     * Returns the list of console commands that will be executed if the click is a left-click.
     *
     * @return The list of console commands to be executed on left-click.
     */
    List<String> getConsoleLeftCommands();

    /**
     * Returns the list of console commands that will be executed if the player has the required permission.
     *
     * @return The list of console commands to be executed on permission.
     */
    List<String> getConsolePermissionCommands();

    /**
     * Returns the permission the player must have to use the console permission commands.
     *
     * @return The required permission for console permission commands.
     */
    String getConsolePermission();


    void execute(MenuPlugin plugin, ClickType type, Placeholders placeholders, Player player);

}
