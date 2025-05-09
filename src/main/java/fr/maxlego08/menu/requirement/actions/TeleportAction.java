package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportAction extends Action {

    private final MenuPlugin plugin;
    private final Location location;

    public TeleportAction(MenuPlugin plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        this.plugin.getScheduler().runTask(player.getLocation(), () -> player.teleport(location));
    }
}
