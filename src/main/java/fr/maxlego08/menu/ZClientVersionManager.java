package fr.maxlego08.menu;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.version.ClientVersionManager;
import fr.maxlego08.menu.api.utils.version.ClientVersionProvider;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class ZClientVersionManager implements ClientVersionManager, Listener {

    private final List<ClientVersionProvider> providers = new CopyOnWriteArrayList<>();
    private final Map<UUID, MinecraftVersion> cache = new ConcurrentHashMap<>();

    @Override
    public void registerProvider(@NotNull ClientVersionProvider provider) {
        this.providers.add(provider);
        this.cache.clear();
    }

    @Override
    @NotNull
    public Collection<ClientVersionProvider> getProviders() {
        return Collections.unmodifiableList(this.providers);
    }

    @Override
    @NotNull
    public MinecraftVersion getClientVersion(@NotNull Player player) {
        return this.cache.computeIfAbsent(player.getUniqueId(), uuid -> this.resolve(player));
    }

    @Override
    public void invalidate(@NotNull Player player) {
        this.cache.remove(player.getUniqueId());
    }

    /**
     * Asks each provider in turn. When none can answer, we assume the client matches the
     * server, which keeps servers without any protocol translation plugin unaffected.
     */
    private MinecraftVersion resolve(Player player) {
        for (ClientVersionProvider provider : this.providers) {
            if (!provider.isAvailable()) continue;
            try {
                Optional<MinecraftVersion> version = provider.getClientVersion(player);
                if (version.isPresent()) {
                    if (Configuration.enableDebug) {
                        Logger.info(provider.getName() + " reported client version " + version.get() + " for " + player.getName());
                    }
                    return version.get();
                }
            } catch (Exception exception) {
                Logger.info("Client version provider " + provider.getName() + " failed for " + player.getName() + ": " + exception.getMessage(), Logger.LogType.WARNING);
            }
        }
        return MinecraftVersion.getCurrentVersion();
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        this.cache.remove(event.getPlayer().getUniqueId());
    }
}
