package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
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

    private PacketAnimationListener packetAnimationListener;
    private PacketTitleListener packetTitleListener;
    private PacketEventClickLimiterListener packetEventClickLimiterListener;

    public static final Map<UUID, FakeInventory> fakeContents = new HashMap<>();
    private final MenuPlugin plugin;

    private boolean ownsApi;
    private boolean ready;

    public PacketUtils(MenuPlugin plugin) {
        this.plugin = plugin;
    }


    private PacketEventsAPI<?> api() {
        PacketEventsAPI<?> api = PacketEvents.getAPI();
        if (api == null) {
            throw new IllegalStateException("The packetevents API is not available, the packetevents plugin most likely failed to load.");
        }
        return api;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public boolean isReady() {
        return this.ready && PacketEvents.getAPI() != null;
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onLoad() {
        if (PacketEvents.getAPI() == null) {
            PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this.plugin));
            this.ownsApi = true;
        }
        if (this.ownsApi) {
            this.api().load();
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onEnable() {
        if (PacketEvents.getAPI() == null) {
            Logger.info("The packetevents API is not available, packet features are disabled.", Logger.LogType.WARNING);
            return;
        }

        if (this.ownsApi) {
            this.api().init();
        }

        EventManager eventManager = this.api().getEventManager();
//         eventManager.registerListener(new PacketListener(), PacketListenerPriority.LOW);
        eventManager.registerListener(this.packetAnimationListener = new PacketAnimationListener(this.plugin), PacketListenerPriority.LOW);
        eventManager.registerListener(this.packetTitleListener = new PacketTitleListener(), PacketListenerPriority.LOW);
        if (Configuration.enablePacketEventClickLimiter){
            this.packetEventClickLimiterListener = new PacketEventClickLimiterListener();
            eventManager.registerListener(this.packetEventClickLimiterListener, PacketListenerPriority.HIGH);
        }

        this.ready = true;
    }

    @Override
    public void onPostEnable() {
        if (this.packetEventClickLimiterListener != null) {
            this.plugin.getInventoryManager().registerInventoryListener(this.packetEventClickLimiterListener);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onDisable() {
        this.ready = false;
        if (this.ownsApi && PacketEvents.getAPI() != null) {
            this.api().terminate();
        }
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
        if (!this.isReady()) return;

        this.packetTitleListener.getPlayerPacketInformation(player.getUniqueId()).ifPresent(playerPacketInformation -> {
            WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow = playerPacketInformation.getWrapperPlayServerOpenWindow();
            WrapperPlayServerOpenWindow newWrapperPlayServerOpenWindow1 = new WrapperPlayServerOpenWindow(wrapperPlayServerOpenWindow.getContainerId(),
                    wrapperPlayServerOpenWindow.getType(),
                    title);
            PlayerManager playerManager = this.api().getPlayerManager();
            playerManager.sendPacket(player, newWrapperPlayServerOpenWindow1);
            playerManager.sendPacket(player, playerPacketInformation.getWrapperPlayServerWindowItems());
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
