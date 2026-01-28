package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetPlayerInventory;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.api.utils.ClearInvType;
import fr.maxlego08.menu.common.utils.nms.ItemStackUtils;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class PacketEventPlayerInventoryManager implements InventoryListener {
    private final Map<UUID, Map<Integer,WrapperPlayServerSetPlayerInventory>> pendingInventoryUpdates = new HashMap<>();

    public PacketEventPlayerInventoryManager() {
        ClearInvType packetEvent = ClearInvType.PACKET_EVENT;
        packetEvent.setOnButtonClear((player, slot)->this.addItemInstantly(player,slot,new ItemStack(Material.AIR)));
        packetEvent.setOnInventoryClose(((inventoriesPlayer, player) -> {
            Optional<InventoryPlayer> playerInventory = inventoriesPlayer.getPlayerInventory(player.getUniqueId());
            if (playerInventory.isPresent()) {
                Map<Integer, String> items = playerInventory.get().getItems();
                for (var entry : items.entrySet()) {
                    int slot = entry.getKey();
                    ItemStack itemStack = ItemStackUtils.deserializeItemStack(entry.getValue());
                    this.addItemInstantly(player, slot,itemStack);
                }
            }
            inventoriesPlayer.clearInventorie(player.getUniqueId());
        }));
        packetEvent.setRemoveItem((player, slot, playerInventory) -> this.addItemLater(player, slot, new ItemStack(Material.AIR)));
    }

    public boolean addItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe){
        if (baseInventory.getClearInvType() == ClearInvType.PACKET_EVENT && inPlayerInventory && baseInventory.getPlayer() != null) {
            WrapperPlayServerSetPlayerInventory wrapperPlayServerSetPlayerInventory = new WrapperPlayServerSetPlayerInventory(itemButton.getSlot(), SpigotConversionUtil.fromBukkitItemStack(itemButton.getDisplayItem()));
            this.pendingInventoryUpdates.computeIfAbsent(baseInventory.getPlayer().getUniqueId(), k -> new HashMap<>()).put(itemButton.getSlot(), wrapperPlayServerSetPlayerInventory);
            return true;
        }
        return false;
    }

    public void onInventoryPreOpen(Player player, BaseInventory baseInventory, int page, Object... objects){
        this.pendingInventoryUpdates.remove(player.getUniqueId());
    }

    public void onInventoryPostOpen(Player player, BaseInventory baseInventory){
        UUID playerUniqueId = player.getUniqueId();
        if (this.pendingInventoryUpdates.containsKey(playerUniqueId)) {
            Map<Integer,WrapperPlayServerSetPlayerInventory> wrappers = this.pendingInventoryUpdates.get(playerUniqueId);
            for (WrapperPlayServerSetPlayerInventory wrapper : wrappers.values()) {
                PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
            }
            this.pendingInventoryUpdates.remove(playerUniqueId);
        }
    }

    public void onButtonClick(Player player, ItemButton button){
        this.addItemInstantly(player, button.getSlot(), button.getDisplayItem());

    }

    public void addItemInstantly(@NotNull Player player, int slot, @NotNull ItemStack itemStack){
        WrapperPlayServerSetPlayerInventory wrapper = new WrapperPlayServerSetPlayerInventory(slot, itemStack.getType() == Material.AIR ? null : SpigotConversionUtil.fromBukkitItemStack(itemStack));
        PacketEvents.getAPI().getPlayerManager().sendPacket(player, wrapper);
    }

    public void addItemLater(@NotNull Player player, int slot, @NotNull ItemStack itemStack){
        this.pendingInventoryUpdates.computeIfAbsent(player.getUniqueId(), k -> new HashMap<>()).put(slot, new WrapperPlayServerSetPlayerInventory(slot, itemStack.getType() == Material.AIR ? null : SpigotConversionUtil.fromBukkitItemStack(itemStack)));
    }
}