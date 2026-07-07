package fr.maxlego08.menu.api.utils;

import fr.maxlego08.menu.api.interfaces.TriConsumer;
import fr.maxlego08.menu.api.players.inventory.InventoriesPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.function.BiConsumer;

public enum ClearInvType {
    DEFAULT(InventoriesPlayer::giveInventory,(player, slot) -> player.getInventory().setItem(slot, new ItemStack(Material.AIR)),(player, slot, playerInventory) -> playerInventory.clear(slot)),
    PACKET_EVENT("packetevents")
    ;

    private BiConsumer<InventoriesPlayer, Player> onInventoryClose;
    private BiConsumer<Player,Integer > onButtonClear;
    private TriConsumer<Player, Integer, PlayerInventory> removeItem;
    private final String requiredPlugin;

    ClearInvType(String requiredPlugin) {
        this.requiredPlugin = requiredPlugin;
        this.onInventoryClose = (inventoriesPlayer, player) -> {
            // No action
        };
        this.onButtonClear = (slot, player) -> {
            // No action
        };
        this.removeItem = (player, slot, playerInventory) -> {
            // No action
        };
    }

    ClearInvType(BiConsumer<InventoriesPlayer, Player> onInventoryClose, BiConsumer<Player, Integer> onButtonClear, TriConsumer<Player, Integer, PlayerInventory> removeItem) {
        this.onInventoryClose = onInventoryClose;
        this.onButtonClear = onButtonClear;
        this.removeItem = removeItem;
        this.requiredPlugin = null;
    }

    public BiConsumer<InventoriesPlayer, Player> getOnInventoryClose() {
        return this.onInventoryClose;
    }

    public void setOnInventoryClose(BiConsumer<InventoriesPlayer, Player> onInventoryClose) {
        this.onInventoryClose = onInventoryClose;
    }

    public BiConsumer<Player, Integer> getOnButtonClear() {
        return this.onButtonClear;
    }

    public void setOnButtonClear(BiConsumer<Player, Integer> onButtonClear) {
        this.onButtonClear = onButtonClear;
    }

    public TriConsumer<Player, Integer, PlayerInventory> getRemoveItem() {
        return this.removeItem;
    }

    public void setRemoveItem(TriConsumer<Player, Integer, PlayerInventory> removeItem) {
        this.removeItem = removeItem;
    }

    public String getRequiredPlugin() {
        return this.requiredPlugin;
    }

    public boolean hasRequiredPlugin() {
        return this.requiredPlugin != null && !this.requiredPlugin.isEmpty();
    }
}
