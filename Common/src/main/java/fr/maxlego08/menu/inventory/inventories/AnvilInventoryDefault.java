package fr.maxlego08.menu.inventory.inventories;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.AnvilInventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.inventory.AnvilInventory;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.TextChange;
import fr.maxlego08.menu.api.utils.TextChangeType;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.common.network.NMSMenuPacketListener;
import fr.maxlego08.menu.common.network.PacketQueue;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.network.protocol.game.ServerboundRenameItemPacket;
import net.minecraft.world.item.ItemStack;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AnvilInventoryDefault extends InventoryDefault implements AnvilInventoryEngine {
    private PacketQueue<Packet<? super ServerGamePacketListener>> incomingPackets;
    private String currentText = "";
    private volatile int containerId;


    private boolean firstPacketReceived = false;

    private TextChange updateText(String newText) {
        if (newText == null) newText = "";
        TextChange change = TextChange.compute(currentText, newText);
        currentText = newText;
        return change;
    }

    @Override
    protected void onPostOpen(Player player, MenuPlugin plugin, int page, Object[] objects) {

        this.containerId = ((CraftPlayer) player).getHandle().containerMenu.containerId;

        super.onPostOpen(player, plugin, page, objects);
    }

    @Override
    public @NotNull String getCurrentText() {
        return this.currentText;
    }

    @Override
    public void postOpen(MenuPlugin plugin, Player player, int page, Object[] objects) {
        incomingPackets = PacketQueue.<Packet<? super ServerGamePacketListener>>builder()
                .on(ServerboundRenameItemPacket.class, packet -> {
                    if (!(this.getMenuInventory() instanceof AnvilInventory anvilInventory)) return;

                    TextChange textChange = updateText(packet.getName());

                    if (!firstPacketReceived && textChange.type() == TextChangeType.EQUAL) {
                        // The client sends an initial packet with the default text when the menu is opened, ignore it
                        firstPacketReceived = true;
                        return;
                    }

                    Placeholders placeholders = new Placeholders();

                    ItemButton item = this.getItem(2);

                    if (item != null) {
                        ItemStack nmsCopy = CraftItemStack.asNMSCopy(item.getDisplayItem());
                        ClientboundContainerSetSlotPacket clientboundContainerSetSlotPacket = new ClientboundContainerSetSlotPacket(
                                containerId,
                                8,
                                2,
                                nmsCopy
                        );

                        NMSMenuPacketListener.get().sendPacket(player, clientboundContainerSetSlotPacket);
                    }

                    placeholders.register("type", textChange.type().name());
                    placeholders.register("old_text", textChange.oldText());
                    switch (textChange.type()) {
                        case ADDED, REMOVED -> {
                            placeholders.register("char", String.valueOf(textChange.changedChar()));
                            placeholders.register("new_text", textChange.newText());
                        }
                        case CLEARED -> placeholders.register("new_text", "");
                        case REPLACED, EQUAL -> placeholders.register("new_text", textChange.newText());
                    }

                    for (Requirement requirement : anvilInventory.getRenameRequirements()) {
                        requirement.execute(player, null, this, placeholders);
                    }

                    List<Button> buttons = getButtons();
                    for (Button button : buttons) {
                        button.onAnvilTextChange(player, this, textChange, placeholders);
                    }

                })
                .build()
                .schedule(plugin, player);

        NMSMenuPacketListener.get().redirectIncoming(player, ServerboundRenameItemPacket.class, incomingPackets);

        super.postOpen(plugin, player, page, objects);
    }

    @Override
    protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {
        NMSMenuPacketListener.get().stopRedirecting(player, ServerboundRenameItemPacket.class);
        incomingPackets.cancel();
        super.onClose(event, plugin, player);
    }

    @Override
    protected void onInventorySwitch(InventoryCloseEvent event, Player player, VInventory newInventoryEngine) {
        // Don't
        if (!(newInventoryEngine instanceof AnvilInventoryEngine)) {
            this.onClose(event, this.plugin, player);
        } else {
            incomingPackets.cancel();
            super.onClose(event, this.plugin, player);
        }
    }
}