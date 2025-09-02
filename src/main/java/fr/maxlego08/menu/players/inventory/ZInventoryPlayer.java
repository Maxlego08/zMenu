package fr.maxlego08.menu.players.inventory;

import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ZInventoryPlayer implements InventoryPlayer {

    private final Map<Integer, String> items = new HashMap<>();

    @Override
    public void storeInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        ItemStack[] content = playerInventory.getContents();
        for (int slot = 0; slot != 36; slot++) {
            clear(slot, playerInventory, content);
        }
        if (!NMSUtils.isOneHand()) {
            clear(40, playerInventory, content);
        }
    }

    private void clear(int slot, PlayerInventory playerInventory, ItemStack[] content) {
        ItemStack itemStack = content[slot];
        if (itemStack != null) {
            items.put(slot, ItemStackUtils.serializeItemStack(itemStack));
        }
        playerInventory.clear(slot);
    }

    @Override
    public void giveInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        items.forEach((slot, encodedItemStack) -> playerInventory.setItem(slot, ItemStackUtils.deserializeItemStack(encodedItemStack)));
    }

    @Override
    public String toInventoryString() {
        StringBuilder builder = new StringBuilder();
        this.items.forEach((slot, itemStack) -> builder.append(slot).append(":").append(itemStack).append(";"));
        String result = builder.toString();
        return result.isEmpty() ? result : result.substring(0, result.length() - 1);
    }

    @Override
    public List<ItemStack> getItemStacks() {
        return this.items.values().stream().map(ItemStackUtils::deserializeItemStack).toList();
    }

    @Override
    public Map<Integer, String> getItems() {
        return this.items;
    }
}
