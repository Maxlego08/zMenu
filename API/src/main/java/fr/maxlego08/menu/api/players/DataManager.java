package fr.maxlego08.menu.api.players;

import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * <p>Player Data Management</p>
 *
 * @deprecated use {@link fr.maxlego08.menu.api.variable.VariableService} with
 * {@link fr.maxlego08.menu.api.variable.VariableScope#PLAYER}: it also handles server and global
 * variables, and adds to numbers atomically ({@code VariableService#increment}).
 */
@Deprecated(since = "1.1.2.2", forRemoval = true)
public interface DataManager {

    /**
     * The data of a player, if it is in memory. It always is for online players; an offline
     * player is only in memory for a few minutes after being looked up, see {@link #loadPlayer(UUID)}.
     *
     * @param uniqueId Player {@link UUID}
     * @return optional
     */
    @NotNull
    Optional<fr.maxlego08.menu.api.players.PlayerData> getPlayer(@NotNull UUID uniqueId);

    /**
     * Reads the data of a player from the database, unless it is already in memory, and keeps it
     * there. Use it before reading or changing the data of an offline player.
     *
     * @param uniqueId Player {@link UUID}
     * @return completes with the data of the player, on a background thread
     */
    @NotNull
    CompletableFuture<PlayerData> loadPlayer(@NotNull UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @return PlayerData
     */
    @NotNull
    PlayerData getOrCreate(@NotNull UUID uniqueId);

    /**
     * @param uniqueId Player {@link UUID}
     * @param data     New data
     */
    void addData(@NotNull UUID uniqueId,@NotNull Data data);

    /**
     * @param uniqueId Player {@link UUID}
     * @param key      Data key
     * @return Optional
     */
    @NotNull
    Optional<Data> getData(@NotNull UUID uniqueId,@NotNull String key);

    List<String> getKeys(@NotNull UUID playerId);

    /**
     * Clear all player's data
     */
    void clearAll();

    /**
     * Clear player's data
     *
     * @param uniqueId Player {@link UUID}
     */
    void clearPlayer(@NotNull UUID uniqueId);

    /**
     * Loads default values for each player.
     * This method is called when the plugin is started,
     * and should be used to load default values for each player.
     */
    void loadDefaultValues();

    /**
     * Reads the data of the players already online, when the plugin starts. Other players are read
     * when they join or when {@link #loadPlayer(UUID)} asks for them.
     */
    void loadPlayers();

    /**
     * Returns a list of all keys that are used in the player data.
     * These keys are used to store and retrieve data from the player data.
     *
     * @return a list of all keys that are used in the player data.
     */
    @NotNull
    List<String> getKeys();

    void clearKey(@NotNull String key);

    void convertOldDatas(@NotNull CommandSender sender);
}
