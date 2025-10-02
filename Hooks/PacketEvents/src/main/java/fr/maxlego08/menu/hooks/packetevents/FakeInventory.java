package fr.maxlego08.menu.hooks.packetevents;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class FakeInventory {

    private final Map<Integer, ItemStack> slots = new HashMap<>();
    private Inventory inventory;

    public FakeInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public Map<Integer, ItemStack> getSlots() {
        return slots;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public void clear() {
        this.slots.clear();
    }

    public void put(int slot, ItemStack itemStack) {
        this.slots.put(slot + this.inventory.size(), itemStack);
    }

    public void forEach(BiConsumer<Integer, ItemStack> biConsumer) {
        this.slots.forEach(biConsumer);
    }

    public ItemStack getAt(int slot) {
        Logger.info("try to " + slot + " -> " + (slot - this.inventory.size()) + " = " + this.slots.get(slot - this.inventory.size()) + " -: " + this.inventory.size());
        return this.slots.get(slot - this.inventory.size());
    }

    public ItemStack getFrom(int slot) {
        Logger.info("try get from " + slot + " -> " + (slot + this.inventory.size()) + " = " + this.slots.get(slot + this.inventory.size()) + " -: " + this.inventory.size());
        return this.slots.getOrDefault(slot + this.inventory.size(), new ItemStack(Material.AIR));
    }
}
