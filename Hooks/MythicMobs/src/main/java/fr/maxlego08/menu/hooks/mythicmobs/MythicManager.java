package fr.maxlego08.menu.hooks.mythicmobs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoListener;
import fr.maxlego08.menu.api.annotations.RequiresPlugin;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

@AutoListener
@RequiresPlugin(value = "MythicMobs", checkMode = RequiresPlugin.CheckMode.EXISTS_AND_ENABLED)
public class MythicManager implements Listener {
    private final MenuPlugin plugin;

    public MythicManager(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("zMenuPlayerData")) {
            event.register(new DataMechanic(event.getConfig(), this.plugin));
        }
    }
}
