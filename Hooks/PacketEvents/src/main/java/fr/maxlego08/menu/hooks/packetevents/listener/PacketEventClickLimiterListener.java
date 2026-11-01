package fr.maxlego08.menu.hooks.packetevents.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.BaseInventory;
import org.bukkit.entity.Player;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketEventClickLimiterListener implements PacketListener, InventoryListener {
    private final Map<UUID, Long> lastClickTimes = new ConcurrentHashMap<>();
    private final Set<UUID> clickLimitedPlayers = ConcurrentHashMap.newKeySet();
    private final MenuPlugin plugin;

    public PacketEventClickLimiterListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!Configuration.enablePacketEventClickLimiter) return;
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Client.CLIENT_STATUS) {
            WrapperPlayClientClientStatus status = new WrapperPlayClientClientStatus(event);
            if (status.getAction() == WrapperPlayClientClientStatus.Action.PERFORM_RESPAWN && event.getUser().getUUID() != null) {
                this.clear(event.getUser().getUUID());
            }
        } else if (packetType == PacketType.Play.Client.CLICK_WINDOW) {
            Player player = event.getPlayer();
            if (player == null) return;
            UUID playerUniqueId = player.getUniqueId();

            if (this.clickLimitedPlayers.contains(playerUniqueId)) {
                long currentTime = System.currentTimeMillis();
                Long lastClickTime = this.lastClickTimes.get(playerUniqueId);
                if (lastClickTime != null && (currentTime - lastClickTime) < Configuration.packetEventClickLimiterMilliseconds) {
                    event.setCancelled(true);
                    this.plugin.getScheduler().runAtEntity(player, task -> {
                        if (player.isOnline()) player.updateInventory();
                    });
                    return;
                }
                this.lastClickTimes.put(playerUniqueId, currentTime);
            }
        }
    }

    @Override
    public void onInventoryPostOpen(Player player, BaseInventory inventory) {
        if (inventory.isClickLimiterEnabled()) {
            this.clickLimitedPlayers.add(player.getUniqueId());
        } else {
            this.clear(player.getUniqueId());
        }
    }

    @Override
    public void onInventoryClose(Player player, BaseInventory inventory) {
        this.clear(player.getUniqueId());
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID playerUniqueId = event.getUser().getUUID();
        if (playerUniqueId != null) {
            this.clear(playerUniqueId);
        }
    }

    private void clear(UUID playerUniqueId) {
        this.clickLimitedPlayers.remove(playerUniqueId);
        this.lastClickTimes.remove(playerUniqueId);
    }
}
