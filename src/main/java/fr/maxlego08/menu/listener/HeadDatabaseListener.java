package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.MenuPlugin;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabaseListener implements Listener {

    private final MenuPlugin plugin;

    /**
     * @param plugin
     */
    public HeadDatabaseListener(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onRead(DatabaseLoadEvent event) {
        this.plugin.getSavers().forEach(saver -> saver.load(this.plugin.getPersist()));
    }

}
