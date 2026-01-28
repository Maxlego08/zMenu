package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class TeleportAction extends Action {

    private final MenuPlugin plugin;
    private final Location location;

    public TeleportAction(MenuPlugin plugin, Location location) {
        this.plugin = plugin;
        this.location = location;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.plugin.getScheduler().runAtEntity(player, w -> plugin.getScheduler().teleportAsync(player, location));
    }
}
