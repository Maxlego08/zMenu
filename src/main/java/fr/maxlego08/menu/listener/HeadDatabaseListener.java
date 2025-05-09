package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.ZMenuPlugin;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabaseListener implements Listener {

    private final ZMenuPlugin plugin;

    /**
     * @param plugin
     */
    public HeadDatabaseListener(ZMenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onRead(DatabaseLoadEvent event) {
        this.plugin.getSavers().forEach(saver -> saver.load(this.plugin.getPersist()));
    }

}
