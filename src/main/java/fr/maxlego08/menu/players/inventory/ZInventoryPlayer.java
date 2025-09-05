package fr.maxlego08.menu.players.inventory;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.players.inventory.InventoryPlayer;
import fr.maxlego08.menu.zcore.utils.nms.ItemStackUtils;
import fr.maxlego08.menu.zcore.utils.nms.NMSUtils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ZInventoryPlayer implements InventoryPlayer {

    private final Map<Integer, String> items = new HashMap<>();
    private final ZMenuPlugin plugin;

    public ZInventoryPlayer(ZMenuPlugin plugin) {
        this.plugin = plugin;
    }

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
    public void forceGiveInventory(Player player) {
        PlayerInventory playerInventory = player.getInventory();
        for (int slot = 0; slot <= 36; slot++) {
            if (items.containsKey(slot)) {
                playerInventory.setItem(slot, ItemStackUtils.deserializeItemStack(items.get(slot)));
            } else if (this.plugin.getDupeManager().isDupeItem(playerInventory.getItem(slot))) {
                playerInventory.setItem(slot, null);
            }
        }
    }

    @Override
    public void setItems(Map<Integer, ItemStack> items) {
        setItemsFromEncode(items.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> ItemStackUtils.serializeItemStack(entry.getValue())
                ))
        );
    }

    @Override
    public void setItemsFromEncode(Map<Integer, String> items) {
        this.items.clear();
        this.items.putAll(items);
    }

    @Override
    public void setItems(List<ItemStack> items) {
        this.items.clear();
        for (int slot = 0; slot != Math.min(items.size(), 36); slot++) {
            ItemStack itemStack = items.get(slot);
            if (itemStack != null) {
                this.items.put(slot, ItemStackUtils.serializeItemStack(itemStack));
            }
        }
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
