package fr.maxlego08.menu.hooks.viaversion;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import fr.maxlego08.menu.api.utils.version.ClientVersionProvider;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class ViaVersionClientVersionProvider implements ClientVersionProvider {

    @Override
    @NotNull
    public String getName() {
        return "ViaVersion";
    }

    @Override
    public boolean isAvailable() {
        try {
            return Via.getAPI() != null;
        } catch (Throwable throwable) {
            return false;
        }
    }

    @Override
    @NotNull
    public Optional<MinecraftVersion> getClientVersion(@NotNull Player player) {
        try {
            int protocol = Via.getAPI().getPlayerVersion(player.getUniqueId());
            if (protocol < 0) return Optional.empty();

            ProtocolVersion protocolVersion = ProtocolVersion.getProtocol(protocol);
            if (!protocolVersion.isKnown()) return Optional.empty();

            return Optional.of(MinecraftVersion.parse(protocolVersion.getName()));
        } catch (Throwable throwable) {
            return Optional.empty();
        }
    }
}
