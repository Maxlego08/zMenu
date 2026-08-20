package fr.maxlego08.menu.api.utils.version;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

/**
 * Resolves the Minecraft version of a player's client through the registered
 * {@link ClientVersionProvider}s, which are queried in registration order.
 * <p>
 * Providers are ordered from most to least trustworthy: protocol translation plugins first,
 * then Paper's own handshake protocol number. When no provider is able to answer, the
 * manager falls back to the server version, so a server with no usable source of client
 * versions behaves as if every client matched it.
 */
public interface ClientVersionManager {

    /**
     * The first Minecraft version able to render the Paper Dialog API.
     */
    MinecraftVersion DIALOG_MINIMUM_VERSION = MinecraftVersion.parse("1.21.6");

    /**
     * Registers a new provider. Providers are queried in registration order.
     *
     * @param provider the provider to register
     */
    void registerProvider(@NotNull ClientVersionProvider provider);

    /**
     * @return the registered providers, in registration order.
     */
    @NotNull
    Collection<ClientVersionProvider> getProviders();

    /**
     * Returns the version of the player's client, or the server version when no provider
     * could determine it.
     *
     * @param player the player to inspect
     * @return the client version, never null
     */
    @NotNull
    MinecraftVersion getClientVersion(@NotNull Player player);

    /**
     * @param player the player to inspect
     * @return true if the player's client can display dialogs (Minecraft 1.21.6 or above).
     */
    default boolean supportsDialogs(@NotNull Player player) {
        return this.getClientVersion(player).isAtLeast(DIALOG_MINIMUM_VERSION);
    }

    /**
     * A manager with no provider, reporting the server version for every player. Used as the
     * default for {@link fr.maxlego08.menu.api.MenuPlugin#getClientVersionManager()} so an
     * implementation predating this interface keeps working.
     */
    ClientVersionManager SERVER_VERSION_ONLY = new ClientVersionManager() {

        @Override
        public void registerProvider(@NotNull ClientVersionProvider provider) {
        }

        @Override
        @NotNull
        public Collection<ClientVersionProvider> getProviders() {
            return List.of();
        }

        @Override
        @NotNull
        public MinecraftVersion getClientVersion(@NotNull Player player) {
            return MinecraftVersion.getCurrentVersion();
        }
    };
}
