package fr.maxlego08.menu.hooks.packetevents.feature;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSession;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSessionStore;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Keeps the last window packets of each player so the title of the open window can be changed
 * by sending it again with a new title, followed by its content.
 */
public class TitleFeature {

    private final PacketSessionStore sessions;

    public TitleFeature(PacketSessionStore sessions) {
        this.sessions = sessions;
    }

    public void onOpenWindow(@NotNull PacketSession session, @NotNull WrapperPlayServerOpenWindow wrapper) {
        session.setLastOpenWindow(wrapper);
    }

    public void onCloseWindow(@NotNull PacketSession session) {
        session.clearWindow();
    }

    public void onWindowItems(@NotNull PacketSession session, @NotNull WrapperPlayServerWindowItems wrapper) {
        session.setLastWindowItems(wrapper);
    }

    @SuppressWarnings("ConstantConditions")
    public void editTitle(@NotNull Player player, @NotNull Component title) {
        PacketSession session = this.sessions.get(player.getUniqueId());
        if (session == null) return;
        WrapperPlayServerOpenWindow openWindow = session.getLastOpenWindow();
        if (openWindow == null) return;

        WrapperPlayServerOpenWindow newOpenWindow = new WrapperPlayServerOpenWindow(openWindow.getContainerId(), openWindow.getType(), title);
        PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();
        playerManager.sendPacket(player, newOpenWindow);
        playerManager.sendPacket(player, session.getLastWindowItems());
    }
}
