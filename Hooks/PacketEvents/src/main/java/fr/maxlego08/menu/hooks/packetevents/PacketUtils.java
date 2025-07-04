package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketUtils implements InventoryListener {

    public static final Map<UUID, FakeInventory> fakeContents = new HashMap<>();
    private final MenuPlugin plugin;

    public PacketUtils(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this.plugin));
        PacketEvents.getAPI().load();
    }

    public void onEnable() {
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getEventManager().registerListener(new PacketListener(), PacketListenerPriority.LOW);
    }

    public void onDisable() {
        PacketEvents.getAPI().terminate();
    }

    @Override
    public boolean addItem(BaseInventory inventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe) {

        if (inPlayerInventory && fakeContents.containsKey(inventory.getPlayer().getUniqueId())) {

            ItemStack itemStack = itemButton.getDisplayItem();
            int slot = itemButton.getSlot();

            FakeInventory fakeInventory = fakeContents.get(inventory.getPlayer().getUniqueId());
            fakeInventory.put(slot, itemStack);
            return true;
        }

        return false;
    }

    @Override
    public void onInventoryPreOpen(Player player, BaseInventory inventory, int page, Object... objects) {
        if (inventory instanceof InventoryEngine) {
            fakeContents.put(player.getUniqueId(), new FakeInventory((Inventory) objects[0]));
        }
    }

    @Override
    public void onInventoryPostOpen(Player player, BaseInventory inventory) {
        if (fakeContents.containsKey(inventory.getPlayer().getUniqueId())) {
            FakeInventory fakeInventory = fakeContents.get(inventory.getPlayer().getUniqueId());
            System.out.println("OPEN");
            System.out.println(fakeInventory.getSlots());
        }
    }

    @Override
    public void onInventoryClose(Player player, BaseInventory inventory) {
        this.plugin.getScheduler().runAtEntityLater(player, () -> {
            InventoryHolder newHolder = CompatibilityUtil.getTopInventory(player).getHolder();
            if (newHolder != null && !(newHolder instanceof InventoryEngine)) {
                fakeContents.remove(player.getUniqueId());
            }
        }, 1);
    }

    @Override
    public void onButtonClick(Player player, ItemButton button) {
        // ToDo
    }
}
