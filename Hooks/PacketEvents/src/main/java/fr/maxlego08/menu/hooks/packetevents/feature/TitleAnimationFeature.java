package fr.maxlego08.menu.hooks.packetevents.feature;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.hooks.packetevents.animation.PacketPlayerTitleAnimation;
import fr.maxlego08.menu.hooks.packetevents.animation.PacketTitleAnimation;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSession;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

/**
 * Starts the title animation of a zMenu inventory when its window opens.
 */
public class TitleAnimationFeature {

    private final MenuPlugin plugin;

    public TitleAnimationFeature(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Arms the hold of the window content when the menu about to open shows its items only after its animation.
     */
    public void onMenuPreOpen(@NotNull PacketSession session, @Nullable TitleAnimation menuTitleAnimation) {
        boolean holdItems = menuTitleAnimation instanceof PacketTitleAnimation titleAnimation
                && titleAnimation.getSettings().showItemsAfterAnimation()
                && !titleAnimation.getSettings().titles().isEmpty();
        session.armWindowHold(holdItems);
    }

    public void onOpenWindow(@NotNull Player player, @NotNull PacketSession session, @NotNull WrapperPlayServerOpenWindow wrapper) {
        int containerId = wrapper.getContainerId();
        session.onWindowOpened(containerId);
        if (session.getAnimationContainerId() == containerId) return;

        this.runInventoryTask(player, () -> {
            Inventory topInventory = CompatibilityUtil.getTopInventory(player);
            boolean started = false;
            if (topInventory != null && topInventory.getHolder() instanceof BaseInventory baseInventory && baseInventory.getTitleAnimation() != null) {
                TitleAnimation titleAnimation = baseInventory.getTitleAnimation();
                Inventory inventory = baseInventory.getInventory();
                PlayerTitleAnimation playerTitleAnimation = titleAnimation.playTitleAnimation(this.plugin, containerId, inventory.getType(), inventory.getSize(), wrapper);
                if (playerTitleAnimation != null) {
                    playerTitleAnimation.start(player, Arrays.asList(inventory.getContents()));
                    started = playerTitleAnimation.isStarted();
                }
                baseInventory.setPlayerTitleAnimation(playerTitleAnimation);
                session.setAnimationContainerId(containerId);
                session.setAnimationWindowId(0);
            }
            if (!started) {
                this.release(player, session, containerId);
            }
        });
    }

    /**
     * Sends the held content of a window whose animation did not start, so its items are not hidden forever.
     */
    @SuppressWarnings("ConstantConditions")
    private void release(@NotNull Player player, @NotNull PacketSession session, int containerId) {
        WrapperPlayServerWindowItems held = session.releaseWindow(containerId);
        if (held != null && PacketEvents.getAPI() != null) {
            PacketEvents.getAPI().getPlayerManager().sendPacket(player, held);
        }
    }

    public void onCloseWindow(@NotNull Player player, @NotNull PacketSession session) {
        this.runInventoryTask(player, () -> {
            Inventory topInventory = CompatibilityUtil.getTopInventory(player);
            if (topInventory == null) return;
            if (topInventory.getHolder() instanceof BaseInventory) {
                session.clearAnimation();
            }
        });
    }

    /**
     * Gives the window content to the animation, and holds it back when the animation shows items only at its end.
     * The hold is decided here, on the thread sending the packet, because cancelling it later has no effect.
     */
    public void onWindowItems(@NotNull PacketSendEvent event, @NotNull Player player, @NotNull PacketSession session, @NotNull WrapperPlayServerWindowItems wrapper) {
        int windowId = wrapper.getWindowId();
        if (session.holdWindowItems(windowId, wrapper)) {
            event.setCancelled(true);
        }

        if (session.getAnimationWindowId() == windowId) return;
        session.setAnimationWindowId(windowId);

        this.runInventoryTask(player, () -> {
            Inventory topInventory = CompatibilityUtil.getTopInventory(player);
            if (topInventory == null) return;
            InventoryHolder holder = topInventory.getHolder();
            if (holder instanceof BaseInventory baseInventory && baseInventory.getPlayerTitleAnimation() instanceof PacketPlayerTitleAnimation playerTitleAnimation) {
                playerTitleAnimation.setWrapperPlayServerWindowItems(wrapper);
            }
        });
    }

    private void runInventoryTask(@NotNull Player player, @NotNull Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
            return;
        }
        if (!this.plugin.isEnabled()) return;
        this.plugin.getScheduler().runAtEntity(player, w -> task.run());
    }
}
