package fr.maxlego08.menu.hooks.packetevents.loader;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.hooks.packetevents.action.PacketEventChangeTitleName;
import fr.maxlego08.menu.hooks.packetevents.listener.PacketTitleListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

public class PacketEventChangeTitleNameLoader extends ActionLoader {
    private final PaperMetaUpdater metaUpdater;
    private final PacketTitleListener packetTitleListener;

    public PacketEventChangeTitleNameLoader(PaperMetaUpdater metaUpdater, PacketTitleListener packetTitleListener) {
        super("change-title", "change-title-name");
        this.metaUpdater = metaUpdater;
        this.packetTitleListener = packetTitleListener;
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        String newInventoryName = accessor.getString("inventory-name", "menu");
        return new PacketEventChangeTitleName(this.metaUpdater, this.packetTitleListener, newInventoryName);
    }
}
