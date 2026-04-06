package fr.maxlego08.menu.hooks.packetevents.loader;

import fr.maxlego08.menu.api.PacketManager;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.packetevents.action.PacketEventChangeTitleName;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PacketEventChangeTitleNameLoader extends ActionLoader {
    private final PacketManager packetManager;

    public PacketEventChangeTitleNameLoader(PacketManager packetManager) {
        super("change-title", "change-title-name");
        this.packetManager = packetManager;
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        String newInventoryName = accessor.getString("inventory-name", "menu");
        return new PacketEventChangeTitleName(newInventoryName, this.packetManager);
    }
}
