package fr.maxlego08.menu.hooks;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.event.FastEvent;
import fr.maxlego08.menu.api.event.events.PlayerOpenInventoryEvent;
import main.java.fr.maxlego08.menu.hooks.FloodgateBuilder;

import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.UUID;


public class FloodgateListener extends FastEvent {

    @Override
    public void onPlayerOpenInventory(PlayerOpenInventoryEvent event) {
        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        Inventory menu = event.getInventory();
        if (menu == null) {
            return;
        }

        String menuName = menu.getName();

        UUID playerUuid = player.getUniqueId();

        FloodgateApi floodgateApi = FloodgateApi.getInstance();
        ConfigurationSection menuConfig = menu.getConfig();

        if (floodgateApi.isFloodgatePlayer(playerUuid)) {
            if (menuConfig == null || !menuConfig.contains("floodgate")) {
                return;
            }
            ConfigurationSection floodgateConfig = menuConfig.getConfigurationSection("floodgate");
            if (floodgateConfig == null) {
                return;
            }
            event.setCancelled(true); 
            FloodgateBuilder floodgateBuilder = new FloodgateBuilder();
            floodgateBuilder.buildAndSendMenu(player, menuConfig);
            return;
        }
    }
}

