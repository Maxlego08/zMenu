package fr.maxlego08.menu.hooks.paper;

import fr.maxlego08.menu.api.utils.PlatformType;
import fr.maxlego08.menu.api.utils.version.ClientVersionProvider;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.api.utils.version.ProtocolVersions;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Reads the client version from the protocol number Paper records during the handshake,
 * through {@code Player#getProtocolVersion()} (inherited from
 * {@code com.destroystokyo.paper.network.NetworkClient}). Requires no third-party plugin.
 * <p>
 * This is the last provider consulted on purpose. ViaVersion rewrites the handshake
 * protocol number to the server's own before the server reads it, so on a ViaVersion
 * server this reports the <b>server</b> version rather than the client's. Its value is only
 * trustworthy when no protocol translation plugin answered first.
 */
public class PaperProtocolClientVersionProvider implements ClientVersionProvider {

    @Override
    @NotNull
    public String getName() {
        return "Paper";
    }

    @Override
    public boolean isAvailable() {
        return PlatformType.isPaper();
    }

    @Override
    @NotNull
    public Optional<MinecraftVersion> getClientVersion(@NotNull Player player) {
        try {
            return ProtocolVersions.fromProtocol(player.getProtocolVersion());
        } catch (Throwable throwable) {
            return Optional.empty();
        }
    }
}
