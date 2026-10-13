package fr.maxlego08.menu.hooks.packetevents.action;

import fr.maxlego08.menu.api.PacketManager;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.hooks.packetevents.loader.PacketEventChangeTitleNameLoader;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class PacketEventChangeTitleName extends ActionHelper {
    private final String newInventoryName;
    private final PacketManager packetManager;


    public PacketEventChangeTitleName(String newInventoryName, PacketManager packetManager) {
        this.newInventoryName = newInventoryName;
        this.packetManager = packetManager;
    }

    @Override
    protected void execute(@NotNull Player player, @Nullable Button button, @NotNull InventoryEngine inventoryEngine, @NotNull Placeholders placeholders) {
        this.packetManager.editInventoryTitleName(player, this.papi(placeholders.parse(this.newInventoryName), player));
    }

    @Override
    protected void serializeProperties(@NotNull Map<String, Object> map) {
        map.put("type", "change-title");
        if (!PacketEventChangeTitleNameLoader.DEFAULT_INVENTORY_NAME.equals(this.newInventoryName)) map.put("inventory-name", this.newInventoryName);
    }
}
