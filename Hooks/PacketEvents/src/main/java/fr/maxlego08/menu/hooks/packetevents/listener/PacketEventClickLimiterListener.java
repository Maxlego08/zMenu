package fr.maxlego08.menu.hooks.packetevents.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketEventClickLimiterListener implements PacketListener {
    private final Map<UUID, Long> lastClickTimes = new HashMap<>();

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        if (!Configuration.enablePacketEventClickLimiter) return;
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Client.CLICK_WINDOW) {
            Player player = event.getPlayer();
            if (player == null) return;
            Inventory topInventory = CompatibilityUtil.getTopInventory(player);
            try {
                if (topInventory != null && topInventory.getHolder() instanceof BaseInventory baseInventory && baseInventory.isClickLimiterEnabled()) {
                    UUID playerUniqueId = player.getUniqueId();

                    long currentTime = System.currentTimeMillis();
                    Long lastClickTime = this.lastClickTimes.get(playerUniqueId);
                    if (lastClickTime != null && (currentTime - lastClickTime) < Configuration.packetEventClickLimiterMilliseconds) {
                        event.setCancelled(true);
                        return;
                    }
                    this.lastClickTimes.put(playerUniqueId, currentTime);
                }
            } catch (Exception ignored) {
            }

        } else if (packetType == PacketType.Play.Client.CLOSE_WINDOW) {
            Player player = event.getPlayer();
            UUID playerUniqueId = player.getUniqueId();
            this.lastClickTimes.remove(playerUniqueId);
        }
    }
}
