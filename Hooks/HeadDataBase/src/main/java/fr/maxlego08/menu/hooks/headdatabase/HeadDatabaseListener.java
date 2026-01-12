package fr.maxlego08.menu.hooks.headdatabase;

import fr.maxlego08.menu.api.MenuPlugin;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class HeadDatabaseListener implements Listener {

    private final MenuPlugin plugin;

    public HeadDatabaseListener(MenuPlugin plugin) {
        super();
        this.plugin = plugin;
    }

    @EventHandler
    public void onRead(DatabaseLoadEvent event) {
        this.plugin.getPatternManager().loadActionsPatterns();
        this.plugin.getPatternManager().loadPatterns();
        this.plugin.getInventoryManager().loadInventories();
    }

}
