package fr.maxlego08.menu.api.action;

import fr.maxlego08.menu.api.action.permissible.Permissible;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

/**
 * <p>Documentation <a href="https://docs.zmenu.dev/configurations/buttons#actions">here</a></p>
 * <p>Allows you to perform more advanced actions, you can configure clicks, permissions and actions to perform if the player has permission or not.</p>
 */
public interface Action {

    /**
     * Return the list of <a href="https://hub.spigotmc.org/javadocs/spigot/org/bukkit/event/inventory/ClickType.html">ClickType</a>
     *
     * @return types
     */
    List<ClickType> getClickType();

    /**
     * Returns the action if the player has permission
     *
     * @return {@link ActionClick}
     */
    ActionClick getAllowAction();

    /**
     * Returns the action if the player does not have permission
     *
     * @return {@link ActionClick}
     */
    ActionClick getDenyAction();

    /**
     * Returns the list of permissions that the player must have
     *
     * @return permissibles
     */
    List<Permissible> getPermissibles();

    /**
     * Executes actions
     *
     * @param player Who execute the commands
     * @param type The click type
     */
	void execute(Player player, ClickType type);

    /**
     * Allows to check if a click is possible
     *
     * @param type The click type
     * @return boolean
     */
	boolean isClick(ClickType type);
}
