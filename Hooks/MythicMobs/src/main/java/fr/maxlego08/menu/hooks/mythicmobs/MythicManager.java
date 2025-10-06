package fr.maxlego08.menu.hooks.mythicmobs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.players.DataManager;
import fr.maxlego08.menu.api.storage.StorageManager;
import io.lumine.mythic.bukkit.events.MythicMechanicLoadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class MythicManager implements Listener {
    private final MenuPlugin plugin;
    private final StorageManager storageManager;
    private final DataManager dataManager;

    public MythicManager(MenuPlugin plugin) {
        this.plugin = plugin;
        this.storageManager = plugin.getStorageManager();
        this.dataManager = plugin.getDataManager();
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onMythicMechanicLoad(MythicMechanicLoadEvent event) {
        if (event.getMechanicName().equalsIgnoreCase("zMenuPlayerData")) {
            event.register(new DataMechanic(event.getConfig(), this.storageManager, this.dataManager));
        }
    }
}
