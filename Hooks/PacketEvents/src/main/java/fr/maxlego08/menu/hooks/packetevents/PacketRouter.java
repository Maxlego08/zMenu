package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.event.UserDisconnectEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.client.WrapperPlayClientClientStatus;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.hooks.packetevents.feature.ClickLimiterFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.HiddenInventoryFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.TitleAnimationFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.TitleFeature;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSession;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSessionStore;
import org.bukkit.entity.Player;

import java.util.UUID;

/**
 * The only packetevents listener of zMenu. Each packet is read once and handed to the features.
 * <p>
 * The hidden inventory rewrite runs last, so the window content the other features keep is the one the client sees.
 */
public class PacketRouter implements PacketListener {

    private final PacketSessionStore sessions;
    private final TitleFeature titleFeature;
    private final TitleAnimationFeature titleAnimationFeature;
    private final HiddenInventoryFeature hiddenInventoryFeature;
    private final ClickLimiterFeature clickLimiterFeature;

    public PacketRouter(PacketSessionStore sessions, TitleFeature titleFeature, TitleAnimationFeature titleAnimationFeature,
                        HiddenInventoryFeature hiddenInventoryFeature, ClickLimiterFeature clickLimiterFeature) {
        this.sessions = sessions;
        this.titleFeature = titleFeature;
        this.titleAnimationFeature = titleAnimationFeature;
        this.hiddenInventoryFeature = hiddenInventoryFeature;
        this.clickLimiterFeature = clickLimiterFeature;
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Server.OPEN_WINDOW) {
            Player player = event.getPlayer();
            if (player == null) return;
            PacketSession session = this.sessions.getOrCreate(player.getUniqueId());
            WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(event);
            this.titleFeature.onOpenWindow(session, wrapper);
            this.titleAnimationFeature.onOpenWindow(player, session, wrapper);
        } else if (packetType == PacketType.Play.Server.CLOSE_WINDOW) {
            Player player = event.getPlayer();
            if (player == null) return;
            PacketSession session = this.sessions.get(player.getUniqueId());
            if (session == null) return;
            this.titleFeature.onCloseWindow(session);
            this.titleAnimationFeature.onCloseWindow(player, session);
        } else if (packetType == PacketType.Play.Server.WINDOW_ITEMS) {
            Player player = event.getPlayer();
            if (player == null) return;
            PacketSession session = this.sessions.getOrCreate(player.getUniqueId());
            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
            this.hiddenInventoryFeature.rewriteWindowItems(event, session, wrapper);
            this.titleFeature.onWindowItems(session, wrapper);
            this.titleAnimationFeature.onWindowItems(event, player, session, wrapper);
        } else if (packetType == PacketType.Play.Server.SET_SLOT) {
            PacketSession session = this.sessions.get(event.getUser().getUUID());
            if (session == null) return;
            this.hiddenInventoryFeature.rewriteSetSlot(event, session, new WrapperPlayServerSetSlot(event));
        } else if (packetType == PacketType.Play.Server.SET_PLAYER_INVENTORY) {
            PacketSession session = this.sessions.get(event.getUser().getUUID());
            if (session == null) return;
            this.hiddenInventoryFeature.rewriteSetPlayerInventory(event, session, new WrapperPlayServerSetPlayerInventory(event));
        }
    }

    @Override
    public void onPacketReceive(PacketReceiveEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        if (packetType == PacketType.Play.Client.CLICK_WINDOW) {
            Player player = event.getPlayer();
            if (player == null) return;
            PacketSession session = this.sessions.get(player.getUniqueId());
            if (session == null) return;
            this.clickLimiterFeature.onClickWindow(event, player, session);
        } else if (packetType == PacketType.Play.Client.CLIENT_STATUS) {
            PacketSession session = this.sessions.get(event.getUser().getUUID());
            if (session == null) return;
            WrapperPlayClientClientStatus status = new WrapperPlayClientClientStatus(event);
            if (status.getAction() == WrapperPlayClientClientStatus.Action.PERFORM_RESPAWN) {
                session.clearHiddenInventory();
                session.clearClickLimiter();
            }
        }
    }

    @Override
    public void onUserDisconnect(UserDisconnectEvent event) {
        UUID playerUniqueId = event.getUser().getUUID();
        this.sessions.remove(playerUniqueId);
    }
}
