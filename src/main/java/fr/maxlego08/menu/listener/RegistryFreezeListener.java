package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.registry.RegistryManager;
import fr.maxlego08.menu.registry.ZRegistryManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerLoadEvent;

/**
 * Freezes the static registries once every plugin has had its {@code onEnable}, so that an addon
 * can still register its entries when it enables after zMenu.
 */
@AutoListener
public class RegistryFreezeListener implements Listener {

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        RegistryManager registries = ZMenuPlugin.getInstance().getInventoryManager().getRegistries();
        if (registries instanceof ZRegistryManager manager) {
            manager.freezeStatic();
        }
    }
}
