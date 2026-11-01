package fr.maxlego08.menu.hooks.packetevents.feature;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSession;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSessionStore;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Hides the player inventory with packets for menus using {@link ClearInvType#PACKET_EVENT}.
 * <p>
 * The real items stay in the player inventory on the server. Every slot zMenu changes is kept in the
 * player's {@link PacketSession}, and every outgoing packet that carries a player slot is rewritten from
 * it, so a resync from the server (a cancelled click, {@link Player#updateInventory()}, a state id
 * mismatch) cannot show the real items while the menu is open.
 */
public class HiddenInventoryFeature {

    private static final int PLAYER_STORAGE_SIZE = 36;
    private static final int OFFHAND_SLOT = 40;

    private final MenuPlugin plugin;
    private final PacketSessionStore sessions;

    public HiddenInventoryFeature(MenuPlugin plugin, PacketSessionStore sessions) {
        this.plugin = plugin;
        this.sessions = sessions;
    }

    public void installClearCallbacks() {
        ClearInvType packetEvent = ClearInvType.PACKET_EVENT;
        packetEvent.setOnButtonClear((player, slot) -> this.showNow(player, slot, new ItemStack(Material.AIR)));
        packetEvent.setOnInventoryClose((inventoriesPlayer, player) -> {
            this.reveal(player);
            inventoriesPlayer.clearInventorie(player.getUniqueId());
        });
        packetEvent.setRemoveItem((player, slot, playerInventory) -> this.showLater(player, slot, new ItemStack(Material.AIR)));
    }

    public void showNow(@NotNull Player player, int slot, @NotNull ItemStack itemStack) {
        PacketSession session = this.sessions.getOrCreate(player.getUniqueId());
        WrapperPlayServerSetPlayerInventory wrapper = new WrapperPlayServerSetPlayerInventory(slot, this.hide(session, slot, itemStack));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
    }

    public void showLater(@NotNull Player player, int slot, @NotNull ItemStack itemStack) {
        PacketSession session = this.sessions.getOrCreate(player.getUniqueId());
        WrapperPlayServerSetPlayerInventory wrapper = new WrapperPlayServerSetPlayerInventory(slot, this.hide(session, slot, itemStack));
        session.getOrCreatePendingSlots().put(slot, wrapper);
    }

    /**
     * Shows the item now, or replaces the queued packet when the flush that follows the opening has not run yet.
     */
    public void update(@NotNull Player player, int slot, @NotNull ItemStack itemStack) {
        PacketSession session = this.sessions.getOrCreate(player.getUniqueId());
        if (session.getPendingSlots() != null) {
            this.showLater(player, slot, itemStack);
        } else {
            this.showNow(player, slot, itemStack);
        }
    }

    public void onMenuPreOpen(@NotNull Player player, boolean hidesInventory) {
        PacketSession session = this.sessions.get(player.getUniqueId());
        if (session == null) return;
        session.clearPendingSlots();
        if (!hidesInventory) {
            session.getHiddenSlots().clear();
        }
    }

    public void onMenuPostOpen(@NotNull Player player) {
        PacketSession session = this.sessions.get(player.getUniqueId());
        if (session == null || session.getPendingSlots() == null) return;

        UUID playerUniqueId = player.getUniqueId();
        this.plugin.getScheduler().runAtEntityLater(player, () -> {
            PacketSession current = this.sessions.get(playerUniqueId);
            if (current == null) return;
            Map<Integer, WrapperPlayServerSetPlayerInventory> pending = current.getPendingSlots();
            if (pending == null) return;
            current.clearPendingSlots();
            for (WrapperPlayServerSetPlayerInventory wrapper : pending.values()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
            }
        }, 1);
    }

    public void reveal(@NotNull Player player) {
        PacketSession session = this.sessions.get(player.getUniqueId());
        if (session != null) session.clearHiddenInventory();
        player.updateInventory();
    }

    private com.github.retrooper.packetevents.protocol.item.ItemStack hide(PacketSession session, int slot, ItemStack itemStack) {
        com.github.retrooper.packetevents.protocol.item.ItemStack packetItem = itemStack.getType() == Material.AIR
                ? com.github.retrooper.packetevents.protocol.item.ItemStack.EMPTY
                : SpigotConversionUtil.fromBukkitItemStack(itemStack);
        session.getHiddenSlots().put(slot, packetItem);
        return packetItem;
    }

    public void rewriteWindowItems(@NotNull PacketSendEvent event, @NotNull PacketSession session, @NotNull WrapperPlayServerWindowItems wrapper) {
        Map<Integer, com.github.retrooper.packetevents.protocol.item.ItemStack> hiddenSlots = session.getHiddenSlots();
        if (hiddenSlots.isEmpty()) return;

        List<com.github.retrooper.packetevents.protocol.item.ItemStack> items = wrapper.getItems();
        int windowId = wrapper.getWindowId();
        int topSize = windowId == 0 ? 0 : items.size() - PLAYER_STORAGE_SIZE;
        if (topSize < 0) return;
        if (windowId != 0) session.getTopSizes().put(windowId, topSize);

        boolean changed = false;
        for (Map.Entry<Integer, com.github.retrooper.packetevents.protocol.item.ItemStack> entry : hiddenSlots.entrySet()) {
            int index = toWindowIndex(windowId, topSize, entry.getKey());
            if (index >= 0 && index < items.size()) {
                items.set(index, entry.getValue());
                changed = true;
            }
        }
        if (changed) {
            wrapper.setItems(items);
            event.markForReEncode(true);
        }
    }

    public void rewriteSetSlot(@NotNull PacketSendEvent event, @NotNull PacketSession session, @NotNull WrapperPlayServerSetSlot wrapper) {
        Map<Integer, com.github.retrooper.packetevents.protocol.item.ItemStack> hiddenSlots = session.getHiddenSlots();
        if (hiddenSlots.isEmpty()) return;

        int windowId = wrapper.getWindowId();
        int playerSlot;
        if (windowId == 0) {
            playerSlot = fromWindowIndex(0, wrapper.getSlot());
        } else if (windowId == -2) {
            playerSlot = wrapper.getSlot();
        } else {
            Integer topSize = session.getTopSizes().get(windowId);
            if (topSize == null) return;
            playerSlot = fromWindowIndex(topSize, wrapper.getSlot());
        }
        com.github.retrooper.packetevents.protocol.item.ItemStack hidden = hiddenSlots.get(playerSlot);
        if (hidden != null) {
            wrapper.setItem(hidden);
            event.markForReEncode(true);
        }
    }

    public void rewriteSetPlayerInventory(@NotNull PacketSendEvent event, @NotNull PacketSession session, @NotNull WrapperPlayServerSetPlayerInventory wrapper) {
        com.github.retrooper.packetevents.protocol.item.ItemStack hidden = session.getHiddenSlots().get(wrapper.getSlot());
        if (hidden != null) {
            wrapper.setStack(hidden);
            event.markForReEncode(true);
        }
    }

    /**
     * Converts a player inventory slot (0-8 hotbar, 9-35 storage, 36-39 armor, 40 offhand) to its index
     * in a window. Window 0 is the player's own inventory; other windows show the 36 storage slots after
     * the top inventory and have no armor or offhand slots.
     */
    static int toWindowIndex(int windowId, int topSize, int playerSlot) {
        if (windowId == 0) {
            if (playerSlot >= 0 && playerSlot <= 8) return 36 + playerSlot;
            if (playerSlot >= 9 && playerSlot <= 35) return playerSlot;
            if (playerSlot >= 36 && playerSlot <= 39) return 8 - (playerSlot - 36);
            if (playerSlot == OFFHAND_SLOT) return 45;
            return -1;
        }
        if (playerSlot >= 0 && playerSlot <= 8) return topSize + 27 + playerSlot;
        if (playerSlot >= 9 && playerSlot <= 35) return topSize + playerSlot - 9;
        return -1;
    }

    static int fromWindowIndex(int topSize, int index) {
        if (topSize == 0) {
            if (index >= 36 && index <= 44) return index - 36;
            if (index >= 9 && index <= 35) return index;
            if (index >= 5 && index <= 8) return 36 + (8 - index);
            if (index == 45) return OFFHAND_SLOT;
            return -1;
        }
        int offset = index - topSize;
        if (offset >= 0 && offset < 27) return offset + 9;
        if (offset >= 27 && offset < PLAYER_STORAGE_SIZE) return offset - 27;
        return -1;
    }
}
