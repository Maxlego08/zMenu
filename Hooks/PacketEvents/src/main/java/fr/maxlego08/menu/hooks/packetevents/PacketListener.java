package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.event.PacketSendEvent;
import com.github.retrooper.packetevents.protocol.item.ItemStack;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerSetSlot;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerWindowItems;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PacketListener implements com.github.retrooper.packetevents.event.PacketListener {

    @Override
    public void onPacketSend(PacketSendEvent event) {
        if (event.getPacketType() == PacketType.Play.Server.WINDOW_ITEMS) {
            WrapperPlayServerWindowItems wrapper = new WrapperPlayServerWindowItems(event);
            if (PacketUtils.fakeContents.containsKey(event.getUser().getUUID())) {
                Player player = event.getPlayer();
                List<ItemStack> items = new ArrayList<>();
                org.bukkit.inventory.ItemStack[] topContent = player.getOpenInventory().getTopInventory().getContents();
                for (org.bukkit.inventory.ItemStack item : topContent) {
                    items.add(SpigotConversionUtil.fromBukkitItemStack(item));
                }

                FakeInventory fakeInventory = PacketUtils.fakeContents.get(event.getUser().getUUID());
                for (int i = 0; i != 36; i++) {
                    org.bukkit.inventory.ItemStack itemStack = fakeInventory.getFrom(i);
                    items.add(SpigotConversionUtil.fromBukkitItemStack(itemStack));
                }

                wrapper.setItems(items);
            }
        } else if (event.getPacketType() == PacketType.Play.Server.SET_SLOT) {
            WrapperPlayServerSetSlot wrapper = new WrapperPlayServerSetSlot(event);
            if (PacketUtils.fakeContents.containsKey(event.getUser().getUUID())) {
                int slot = wrapper.getSlot();
                FakeInventory fakeInventory = PacketUtils.fakeContents.get(event.getUser().getUUID());
                org.bukkit.inventory.ItemStack item = fakeInventory.getAt(slot);
                if (item != null) {
                    wrapper.setItem(SpigotConversionUtil.fromBukkitItemStack(item));
                }
            }
        }
    }
}
