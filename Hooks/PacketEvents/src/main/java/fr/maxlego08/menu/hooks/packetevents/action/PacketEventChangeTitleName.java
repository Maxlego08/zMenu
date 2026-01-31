package fr.maxlego08.menu.hooks.packetevents.action;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.manager.player.PlayerManager;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerOpenWindow;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.hooks.packetevents.listener.PacketTitleListener;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PacketEventChangeTitleName extends ActionHelper {
    private final PaperMetaUpdater metaUpdater;
    private final PacketTitleListener packetTitleListener;
    private final String newInventoryName;
    private final PlayerManager playerManager = PacketEvents.getAPI().getPlayerManager();

    public PacketEventChangeTitleName(@NotNull PaperMetaUpdater metaUpdater, PacketTitleListener packetTitleListener, String newInventoryName) {
        this.metaUpdater = metaUpdater;
        this.packetTitleListener = packetTitleListener;
        this.newInventoryName = newInventoryName;
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        Component component = metaUpdater.getComponent(papi(placeholders.parse(this.newInventoryName), player));
        PacketTitleListener.PlayerPacketInformation playerPacketInformation = this.packetTitleListener.getPlayerPacketInformation(player.getUniqueId());
        if (playerPacketInformation != null) {
            WrapperPlayServerOpenWindow wrapperPlayServerOpenWindow = playerPacketInformation.getWrapperPlayServerOpenWindow();
            WrapperPlayServerOpenWindow newWrapperPlayServerOpenWindow1 = new WrapperPlayServerOpenWindow(wrapperPlayServerOpenWindow.getContainerId(),
                    wrapperPlayServerOpenWindow.getType(),
                    component);
            this.playerManager.sendPacket(player, newWrapperPlayServerOpenWindow1);
            this.playerManager.sendPacket(player, playerPacketInformation.getWrapperPlayServerWindowItems());
        }
    }
}
