package fr.maxlego08.menu.hooks;

import com.nexomc.nexo.api.events.NexoItemsLoadedEvent;
import com.nexomc.nexo.glyphs.GlyphTag;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class NexoTagResolverLoader implements Listener {

    private final PaperMetaUpdater paperMetaUpdater;
    private boolean loaded = false;

    public NexoTagResolverLoader(MenuPlugin zMenuPlugin, PaperMetaUpdater paperMetaUpdater) {
        this.paperMetaUpdater = paperMetaUpdater;
        zMenuPlugin.getServer().getPluginManager().registerEvents(this, zMenuPlugin);
    }

    @EventHandler
    public void onNexoLoad(NexoItemsLoadedEvent event) {
        this.paperMetaUpdater.clearCache();
        if (!loaded) {
            loaded = true;
            this.paperMetaUpdater.withTagResolver(GlyphTag.INSTANCE.getRESOLVER());
            this.paperMetaUpdater.buildMiniMessage();
        }
    }

}
