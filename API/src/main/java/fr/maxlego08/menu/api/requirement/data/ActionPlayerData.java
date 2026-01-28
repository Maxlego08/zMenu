package fr.maxlego08.menu.api.requirement.data;

import fr.maxlego08.menu.api.players.Data;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents an action associated with player data.
 */
public interface ActionPlayerData {

    /**
     * Gets the unique key for the action. Ensure keys are unique.
     *
     * @return The key.
     */
    @NotNull
    String getKey();

    /**
     * Gets the type of action.
     *
     * @return The {@link ActionPlayerDataType}.
     */
    @NotNull
    ActionPlayerDataType getType();

    /**
     * Gets the value associated with the action.
     *
     * @return The value.
     */
    @Nullable
    Object getValue();

    /**
     * Gets the number of seconds until the data expires. Use 0 for no expiration.
     *
     * @return The expiration time in seconds.
     */
    @NotNull
    String getSeconds();

    /**
     * Converts the action into player data.
     * <p>
     * DEPRECATED: Use {@link #toData(OfflinePlayer, Placeholders)} instead.
     *
     * @param player The player for whom the data is created.
     * @return The {@link Data}.
     */
    @Deprecated(since = "1.1.0.6")
    @NotNull
    Data toData(@Nullable OfflinePlayer player);

    @NotNull
    Data toData(@Nullable OfflinePlayer player,@NotNull Placeholders placeholders);

    /**
     * Executes the action when the player clicks.
     * <p>
     * DEPRECATED: Use {@link #execute(Player, DataManager, Placeholders)} instead.
     *
     * @param player       The player who executes the action.
     * @param dataManager  The {@link DataManager}.
     */
    @Deprecated(since = "1.1.0.6")
    void execute(@NotNull Player player,@NotNull DataManager dataManager);

    void execute(@NotNull Player player,@NotNull DataManager dataManager,@NotNull Placeholders placeholders);
}
