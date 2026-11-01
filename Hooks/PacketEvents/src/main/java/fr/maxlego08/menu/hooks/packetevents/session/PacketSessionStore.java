package fr.maxlego08.menu.hooks.packetevents.session;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketSessionStore {

    private final Map<UUID, PacketSession> sessions = new ConcurrentHashMap<>();

    public @Nullable PacketSession get(@Nullable UUID playerUniqueId) {
        return playerUniqueId == null ? null : this.sessions.get(playerUniqueId);
    }

    public @NotNull PacketSession getOrCreate(@NotNull UUID playerUniqueId) {
        return this.sessions.computeIfAbsent(playerUniqueId, k -> new PacketSession());
    }

    public void remove(@Nullable UUID playerUniqueId) {
        if (playerUniqueId != null) {
            this.sessions.remove(playerUniqueId);
        }
    }

    public void clear() {
        this.sessions.clear();
    }
}
