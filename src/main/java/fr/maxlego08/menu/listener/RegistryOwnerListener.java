package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

/**
 * Removes what a plugin registered in the open registries when it disables, so that it can
 * register again if it is enabled once more.
 */
@AutoListener
public class RegistryOwnerListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPluginDisable(PluginDisableEvent event) {
        ZMenuPlugin plugin = ZMenuPlugin.getInstance();
        if (event.getPlugin() == plugin) return;
        plugin.getInventoryManager().getRegistries().unregisterOpen(event.getPlugin().getName());
    }
}
