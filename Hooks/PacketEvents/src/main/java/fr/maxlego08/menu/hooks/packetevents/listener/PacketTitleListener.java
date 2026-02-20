package fr.maxlego08.menu.hooks.packetevents.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketTitleListener implements PacketListener {
    private final Map<UUID, PlayerPacketInformation> playerPacketInformation = new HashMap<>();

    public static class PlayerPacketInformation {
        private WrapperPlayServerWindowItems wrapperPlayServerWindowItems;
        private WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow;

        public WrapperPlayServerWindowItems getWrapperPlayServerWindowItems() {
            return wrapperPlayServerWindowItems;
        }

        public void setWrapperPlayServerWindowItems(WrapperPlayServerWindowItems wrapperPlayServerWindowItems) {
            this.wrapperPlayServerWindowItems = wrapperPlayServerWindowItems;
        }

        public WrapperPlayServerOpenWindow getWrapperPlayServerOpenWindow() {
            return wrapperPlayServerOpenWindow;
        }

        public void setWrapperPlayServerOpenWindow(WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow) {
            this.wrapperPlayServerOpenWindow = wrapperPlayServerOpenWindow;
        }
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        //TODO: Only store packets for players who have menus open from zMenu
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.OPEN_WINDOW){
            WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(event);
            Player player = event.getPlayer();
            if (player == null) return;
            UUID playerUniqueId = player.getUniqueId();
            this.playerPacketInformation.computeIfAbsent(playerUniqueId, k -> new PlayerPacketInformation())
                    .setWrapperPlayServerOpenWindow(wrapper);
        } else if (packetType == PacketType.Play.Server.CLOSE_WINDOW){
            Player player = event.getPlayer();
            if (player == null) return;
            UUID playerUniqueId = player.getUniqueId();
            this.playerPacketInformation.remove(playerUniqueId);
        } else if (packetType == PacketType.Play.Server.WINDOW_ITEMS){
            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
            Player player = event.getPlayer();
            if (player == null) return;
            UUID playerUniqueId = player.getUniqueId();
            this.playerPacketInformation.computeIfAbsent(playerUniqueId, k -> new PlayerPacketInformation())
                    .setWrapperPlayServerWindowItems(wrapper);
        }
    }

    public PlayerPacketInformation getPlayerPacketInformation(UUID playerUUID) {
        return this.playerPacketInformation.get(playerUUID);
    }


}
