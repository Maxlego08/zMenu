package fr.maxlego08.menu.hooks.packetevents.listener;

import com.github.retrooper.packetevents.event.PacketListener;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.hooks.packetevents.animation.PacketPlayerTitleAnimation;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PacketAnimationListener implements PacketListener {
    private final Map<UUID, PlayerAnimationData> playerAnimationData = new ConcurrentHashMap<>();
    private final MenuPlugin plugin;

    public PacketAnimationListener(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    private static class PlayerAnimationData {
        private int containerId;
        private int windowId;

        public PlayerAnimationData(int containerId) {
            this.containerId = containerId;
        }

        public int getContainerId() {
            return this.containerId;
        }

        public void setContainerId(int containerId) {
            this.containerId = containerId;
        }

        public int getWindowId() {
            return this.windowId;
        }

        public void setWindowId(int windowId) {
            this.windowId = windowId;
        }
    }

    private void runInventoryTask(@NotNull Player player, @NotNull Runnable task) {
        if (Bukkit.isPrimaryThread()) {
            task.run();
            return;
        }

        if (!this.plugin.isEnabled()) {
            return;
        }

        this.plugin.getScheduler().runAtEntity(player, w -> task.run());
    }

    @Override
    public void onPacketSend(PacketSendEvent event) {
        PacketTypeCommon packetType = event.getPacketType();
        switch (packetType) {
            case PacketType.Play.Server.OPEN_WINDOW -> {
                WrapperPlayServerOpenWindow wrapper = new WrapperPlayServerOpenWindow(event);
                int containerId = wrapper.getContainerId();
                Player player = event.getPlayer();
                if (player == null) return;
                UUID playerUniqueId = player.getUniqueId();

                PlayerAnimationData data = this.playerAnimationData.get(playerUniqueId);
                if (data != null && data.getContainerId() == containerId) {
                    return;
                }

                Runnable task = () -> {
                    Inventory topInventory = CompatibilityUtil.getTopInventory(player);
                    if (topInventory == null) return;
                    InventoryHolder holder = topInventory.getHolder();
                    if (holder instanceof BaseInventory baseInventory) {
                        TitleAnimation titleAnimation = baseInventory.getTitleAnimation();
                        if (titleAnimation == null) {
                            return;
                        }
                        Inventory inventory = baseInventory.getInventory();
                        PlayerTitleAnimation playerTitleAnimation = titleAnimation.playTitleAnimation(this.plugin, containerId, inventory.getType(), inventory.getSize(), wrapper);
                        if (playerTitleAnimation != null) {
                            playerTitleAnimation.start(player, Arrays.asList(inventory.getContents()));
                        }
                        baseInventory.setPlayerTitleAnimation(playerTitleAnimation);
                        this.playerAnimationData.put(playerUniqueId, new PlayerAnimationData(containerId));
                    }
                };

                this.runInventoryTask(player, task);
            }
            case PacketType.Play.Server.CLOSE_WINDOW -> {
                Player player = event.getPlayer();
                if (player == null) return;

                Runnable task = () -> {
                    Inventory topInventory = CompatibilityUtil.getTopInventory(player);
                    if (topInventory == null) return;
                    InventoryHolder holder = topInventory.getHolder();
                    if (holder instanceof BaseInventory) {
                        UUID playerUniqueId = player.getUniqueId();
                        this.playerAnimationData.remove(playerUniqueId);
                    }
                };

                this.runInventoryTask(player, task);
            }
            case PacketType.Play.Server.WINDOW_ITEMS -> {
                WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
                Player player = event.getPlayer();
                if (player == null) return;
                UUID playerUniqueId = player.getUniqueId();

                int windowId = wrapper.getWindowId();

                PlayerAnimationData data = this.playerAnimationData.computeIfAbsent(playerUniqueId, k -> new PlayerAnimationData(0));
                if (data.getWindowId() == windowId) {
                    return;
                }
                data.setWindowId(windowId);

                Runnable task = () -> {
                    Inventory topInventory = CompatibilityUtil.getTopInventory(player);
                    if (topInventory == null) return;
                    InventoryHolder holder = topInventory.getHolder();
                    if (holder instanceof BaseInventory baseInventory && baseInventory.getPlayerTitleAnimation() instanceof PacketPlayerTitleAnimation playerTitleAnimation) {
                        playerTitleAnimation.setWrapperPlayServerWindowItems(wrapper);
                        if (playerTitleAnimation.getSettings().showItemsAfterAnimation()) {
                            event.setCancelled(true);
                        }
                    }
                };

                this.runInventoryTask(player, task);
            }
            default -> {
            }
        }
    }
}