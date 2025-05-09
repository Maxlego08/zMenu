package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.PlayerNamePermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import org.bukkit.entity.Player;

import java.util.List;

public class ZPlayerNamePermissible extends ZPermissible implements PlayerNamePermissible {

    private final String playerName;

    public ZPlayerNamePermissible(String playerName, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.playerName = playerName;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        String name = this.papi(this.playerName.replace("%player%", player.getName()), player, false);
        return isMinecraftName(name);
    }

    @Override
    public boolean isValid() {
        return this.playerName != null;
    }

    @Override
    public String getPlayerName() {
        return this.playerName;
    }
}
