package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.event.EventManager;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryListener;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.PacketManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.hooks.packetevents.listener.PacketAnimationListener;
import fr.maxlego08.menu.hooks.packetevents.listener.PacketEventClickLimiterListener;
import fr.maxlego08.menu.hooks.packetevents.listener.PacketTitleListener;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PacketUtils implements InventoryListener, PacketManager {
    private final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

    private PacketAnimationListener packetAnimationListener;
    private PacketTitleListener packetTitleListener;

    public static final Map<UUID, FakeInventory> fakeContents = new HashMap<>();
    private final MenuPlugin plugin;

    public PacketUtils(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this.plugin));
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        PacketEvents.getAPI().init();
        EventManager eventManager = PacketEvents.getAPI().getEventManager();
//         eventManager.registerListener(new PacketListener(), PacketListenerPriority.LOW);
        eventManager.registerListener(this.packetAnimationListener = new PacketAnimationListener(this.plugin), PacketListenerPriority.LOW);
        eventManager.registerListener(this.packetTitleListener = new PacketTitleListener(), PacketListenerPriority.LOW);
        if (Configuration.enablePacketEventClickLimiter){
            eventManager.registerListener(new PacketEventClickLimiterListener(), PacketListenerPriority.HIGH);
        }
    }

    @Override
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
            Logger.info("OPEN");
            Logger.info(fakeInventory.getSlots().toString());
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

    public PacketAnimationListener getPacketAnimationListener() {
        return this.packetAnimationListener;
    }

    public PacketTitleListener getPacketTitleListener() {
        return this.packetTitleListener;
    }

    @Override
    public void editInventoryTitleName(@NotNull Player player, @NotNull Component title) {
        this.packetTitleListener.getPlayerPacketInformation(player.getUniqueId()).ifPresent(playerPacketInformation -> {
            WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow = playerPacketInformation.getWrapperPlayServerOpenWindow();
            WrapperPlayServerOpenWindow newWrapperPlayServerOpenWindow1 = new WrapperPlayServerOpenWindow(wrapperPlayServerOpenWindow.getContainerId(),
                    wrapperPlayServerOpenWindow.getType(),
                    title);
            this.playerManager.sendPacket(player, newWrapperPlayServerOpenWindow1);
            this.playerManager.sendPacket(player, playerPacketInformation.getWrapperPlayServerWindowItems());
        });
    }

    @Override
    public void editInventoryTitleName(@NotNull Player player, @NotNull String title) {
        if (this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater) {
            Component component = paperMetaUpdater.getComponent(title);
            this.editInventoryTitleName(player, component);
        }
    }
}
