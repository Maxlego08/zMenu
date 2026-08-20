package fr.maxlego08.menu.api.utils.version;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Provides the Minecraft version of a player's <b>client</b>, which can differ from the
 * server version when a protocol translation plugin such as ViaVersion is installed.
 * <p>
 * Implementations live in the {@code Hooks/ClientVersion} module and are registered on
 * {@link ClientVersionManager} when their backing plugin is present.
 */
public interface ClientVersionProvider {

    /**
     * @return the name of the plugin backing this provider, used for logging.
     */
    @NotNull
    String getName();

    /**
     * @return true if the backing plugin is present and its API can be used.
     */
    boolean isAvailable();

    /**
     * Returns the version of the player's client.
     *
     * @param player the player to inspect
     * @return the client version, or {@link Optional#empty()} if it could not be determined
     */
    @NotNull
    Optional<MinecraftVersion> getClientVersion(@NotNull Player player);
}
