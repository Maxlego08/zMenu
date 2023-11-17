package fr.maxlego08.menu.api.requirement.data;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import org.bukkit.entity.Player;

/**
 * Represents an action associated with player data.
 */
public interface ActionPlayerData {

    /**
     * Gets the unique key for the action. Ensure keys are unique.
     *
     * @return The key.
     */
    String getKey();

    /**
     * Gets the type of action.
     *
     * @return The {@link ActionPlayerDataType}.
     */
    ActionPlayerDataType getType();

    /**
     * Gets the value associated with the action.
     *
     * @return The value.
     */
    Object getValue();

    /**
     * Gets the number of seconds until the data expires. Use 0 for no expiration.
     *
     * @return The expiration time in seconds.
     */
    long getSeconds();

    /**
     * Converts the action into player data.
     *
     * @return The {@link Data}.
     */
    Data toData();

    /**
     * Executes the action when the player clicks.
     *
     * @param player       The player who executes the action.
     * @param dataManager  The {@link DataManager}.
     */
    void execute(Player player, DataManager dataManager);
}
