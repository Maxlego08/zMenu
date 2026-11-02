package fr.maxlego08.menu.hooks.packetevents.feature;

import com.github.retrooper.packetevents.event.PacketReceiveEvent;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSession;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Drops click packets sent faster than {@code packet-event-click-limiter-milliseconds} while a zMenu
 * inventory with the click limiter enabled is open.
 * <p>
 * The client has already applied a dropped click on its side, so the inventory is sent again to undo it.
 */
public class ClickLimiterFeature {

    private final MenuPlugin plugin;

    public ClickLimiterFeature(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    public void onClickWindow(@NotNull PacketReceiveEvent event, @NotNull Player player, @NotNull PacketSession session) {
        if (!Configuration.enablePacketEventClickLimiter || !session.isClickLimited()) return;

        long currentTime = System.currentTimeMillis();
        long lastClickTime = session.getLastClickTime();
        if (lastClickTime != 0L && (currentTime - lastClickTime) < Configuration.packetEventClickLimiterMilliseconds) {
            event.setCancelled(true);
            this.plugin.getScheduler().runAtEntity(player, task -> {
                if (player.isOnline()) player.updateInventory();
            });
            return;
        }
        session.setLastClickTime(currentTime);
    }

    public void onMenuOpened(@NotNull PacketSession session, boolean clickLimiterEnabled) {
        if (clickLimiterEnabled) {
            session.setClickLimited(true);
        } else {
            session.clearClickLimiter();
        }
    }

    public void onMenuClosed(@NotNull PacketSession session) {
        session.clearClickLimiter();
    }
}
