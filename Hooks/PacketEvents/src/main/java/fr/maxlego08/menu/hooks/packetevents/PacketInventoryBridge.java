package fr.maxlego08.menu.hooks.packetevents;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.animation.TitleAnimation;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.hooks.packetevents.feature.ClickLimiterFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.HiddenInventoryFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.TitleAnimationFeature;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSessionStore;
import org.bukkit.entity.Player;

/**
 * The only inventory listener of the packetevents hook. Feeds the packet features from menu events.
 */
public class PacketInventoryBridge implements InventoryListener {

    private final PacketSessionStore sessions;
    private final HiddenInventoryFeature hiddenInventoryFeature;
    private final ClickLimiterFeature clickLimiterFeature;
    private final TitleAnimationFeature titleAnimationFeature;

    public PacketInventoryBridge(PacketSessionStore sessions, HiddenInventoryFeature hiddenInventoryFeature, ClickLimiterFeature clickLimiterFeature,
                                 TitleAnimationFeature titleAnimationFeature) {
        this.sessions = sessions;
        this.hiddenInventoryFeature = hiddenInventoryFeature;
        this.clickLimiterFeature = clickLimiterFeature;
        this.titleAnimationFeature = titleAnimationFeature;
    }

    @Override
    public boolean addItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe) {
        Player player = baseInventory.getPlayer();
        if (baseInventory.getClearInvType() == ClearInvType.PACKET_EVENT && inPlayerInventory && player != null) {
            this.hiddenInventoryFeature.showLater(player, itemButton.getSlot(), itemButton.getDisplayItem());
            return true;
        }
        return false;
    }

    @Override
    public boolean updateItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton) {
        Player player = baseInventory.getPlayer();
        if (baseInventory.getClearInvType() == ClearInvType.PACKET_EVENT && inPlayerInventory && player != null) {
            this.hiddenInventoryFeature.update(player, itemButton.getSlot(), itemButton.getDisplayItem());
            return true;
        }
        return false;
    }

    @Override
    public void onInventoryPreOpen(Player player, BaseInventory baseInventory, int page, Object... objects) {
        Inventory menu = objects.length > 0 && objects[0] instanceof Inventory inventory ? inventory : null;
        ClearInvType clearInvType = menu != null ? menu.getClearInvType() : baseInventory.getClearInvType();
        TitleAnimation titleAnimation = menu != null ? menu.getTitleAnimation() : baseInventory.getTitleAnimation();
        this.hiddenInventoryFeature.onMenuPreOpen(player, clearInvType == ClearInvType.PACKET_EVENT);
        this.titleAnimationFeature.onMenuPreOpen(this.sessions.getOrCreate(player.getUniqueId()), titleAnimation);
    }

    @Override
    public void onInventoryPostOpen(Player player, BaseInventory baseInventory) {
        this.hiddenInventoryFeature.onMenuPostOpen(player);
        this.clickLimiterFeature.onMenuOpened(this.sessions.getOrCreate(player.getUniqueId()), baseInventory.isClickLimiterEnabled());
    }

    @Override
    public void onInventoryClose(Player player, BaseInventory baseInventory) {
        var session = this.sessions.get(player.getUniqueId());
        if (session != null) {
            this.clickLimiterFeature.onMenuClosed(session);
        }
    }

    @Override
    public void onButtonClick(Player player, ItemButton button) {
        if (button.isInPlayerInventory() && button.getBaseInventory().getClearInvType() == ClearInvType.PACKET_EVENT) {
            this.hiddenInventoryFeature.showNow(player, button.getSlot(), button.getDisplayItem());
        }
    }
}
