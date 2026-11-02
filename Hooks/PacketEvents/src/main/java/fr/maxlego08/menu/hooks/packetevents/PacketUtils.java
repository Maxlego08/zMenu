package fr.maxlego08.menu.hooks.packetevents;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.PacketEventsAPI;
import com.github.retrooper.packetevents.event.PacketListenerCommon;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.PacketManager;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.hooks.packetevents.feature.ClickLimiterFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.HiddenInventoryFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.TitleAnimationFeature;
import fr.maxlego08.menu.hooks.packetevents.feature.TitleFeature;
import fr.maxlego08.menu.hooks.packetevents.session.PacketSessionStore;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PacketUtils implements PacketManager {

    private final MenuPlugin plugin;
    private final PacketSessionStore sessions = new PacketSessionStore();

    private TitleFeature titleFeature;
    private PacketInventoryBridge inventoryBridge;
    private PacketListenerCommon registeredListener;

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

        this.titleFeature = new TitleFeature(this.sessions);
        TitleAnimationFeature titleAnimationFeature = new TitleAnimationFeature(this.plugin);
        HiddenInventoryFeature hiddenInventoryFeature = new HiddenInventoryFeature(this.plugin, this.sessions);
        ClickLimiterFeature clickLimiterFeature = new ClickLimiterFeature(this.plugin);

        hiddenInventoryFeature.installClearCallbacks();
        this.inventoryBridge = new PacketInventoryBridge(this.sessions, hiddenInventoryFeature, clickLimiterFeature, titleAnimationFeature);

        PacketRouter router = new PacketRouter(this.sessions, this.titleFeature, titleAnimationFeature, hiddenInventoryFeature, clickLimiterFeature);
        this.registeredListener = this.api().getEventManager().registerListener(router, PacketListenerPriority.HIGHEST);

        this.ready = true;
    }

    @Override
    public void onPostEnable() {
        if (this.inventoryBridge != null) {
            this.plugin.getInventoryManager().registerInventoryListener(this.inventoryBridge);
        }
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public void onDisable() {
        this.ready = false;
        this.unregisterListener();
        this.sessions.clear();
        if (this.ownsApi && PacketEvents.getAPI() != null) {
            this.api().terminate();
        }
    }

    /**
     * Removes zMenu's listener from packetevents. packetevents outlives zMenu when it is a plugin
     * of its own, and would otherwise keep calling classes of a disabled zMenu, whose jar is closed.
     */
    @SuppressWarnings("ConstantConditions")
    private void unregisterListener() {
        if (this.registeredListener == null) return;
        PacketEventsAPI<?> api = PacketEvents.getAPI();
        if (api != null) {
            api.getEventManager().unregisterListener(this.registeredListener);
        }
        this.registeredListener = null;
    }

    @Override
    public void editInventoryTitleName(@NotNull Player player, @NotNull Component title) {
        if (!this.isReady()) return;
        this.titleFeature.editTitle(player, title);
    }

    @Override
    public void editInventoryTitleName(@NotNull Player player, @NotNull String title) {
        if (this.plugin.getMetaUpdater() instanceof PaperMetaUpdater paperMetaUpdater) {
            Component component = paperMetaUpdater.getComponent(title);
            this.editInventoryTitleName(player, component);
        }
    }
}
