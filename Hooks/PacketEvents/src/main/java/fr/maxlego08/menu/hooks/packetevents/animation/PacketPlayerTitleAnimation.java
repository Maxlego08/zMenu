package fr.maxlego08.menu.hooks.packetevents.animation;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.animation.PlayerTitleAnimation;
import fr.maxlego08.menu.api.animation.TitleAnimationSettings;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class PacketPlayerTitleAnimation extends PlayerTitleAnimation {
    private final WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow;
    private final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

    private WrapperPlayServerWindowItems wrapperPlayServerWindowItems;

    public PacketPlayerTitleAnimation(MenuPlugin plugin, TitleAnimationSettings settings, int containerId, InventoryType type, int size, Object... args) {
        super(plugin, settings, containerId, type, size);
        this.wrapperPlayServerOpenWindow = (WrapperPlayServerOpenWindow) args[0];
    }

    public void setWrapperPlayServerWindowItems(WrapperPlayServerWindowItems wrapperPlayServerWindowItems) {
        this.wrapperPlayServerWindowItems = wrapperPlayServerWindowItems;
    }

    @Override
    public void sendTitle(@NonNull Player player, @NonNull String title) {
        this.wrapperPlayServerOpenWindow.setTitle(this.metaUpdater.getComponent(title));
        this.playerManager.sendPacket(player, this.wrapperPlayServerOpenWindow);
    }

    @Override
    public void sendInventoryContent(@NotNull Player player, @NotNull List<ItemStack> inventoryContents) {
        if (this.wrapperPlayServerWindowItems != null) {
            this.playerManager.sendPacket(player, this.wrapperPlayServerWindowItems);
        }
    }
}