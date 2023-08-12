package fr.maxlego08.menu.listener;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.save.Config;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;

public class FKeyListener implements Listener {
    @EventHandler
    public void onPressFKey(PlayerSwapHandItemsEvent e) {
        if (Config.useFKeyToOpenMainMenu) {
            InventoryManager im = MenuPlugin.getInstance().getInventoryManager();
            Player player = e.getPlayer();

            if (Config.useFKeyToOpenMainMenuNeedsShift) {
                if (player.isSneaking()) {
                    im.openInventory(player, Config.mainMenu);
                    e.setCancelled(true);
                }
            } else {
                im.openInventory(player, Config.mainMenu);
                e.setCancelled(true);
            }
        }
    }
}
