package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
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
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        this.plugin.getScheduler().runAtLocation(player.getLocation(), w -> player.teleport(location));
    }
}
