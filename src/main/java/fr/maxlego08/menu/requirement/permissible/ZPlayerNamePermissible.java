package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.permissible.PlayerNamePermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZPlayerNamePermissible extends PlayerNamePermissible {

    private final String playerName;

    public ZPlayerNamePermissible(String playerName, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.playerName = playerName;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        String name = inventory.getPlugin().parse(player, this.playerName.replace("%player%", player.getName()));
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

    protected boolean isMinecraftName(String username) {
        String MINECRAFT_USERNAME_REGEX = "^[a-zA-Z0-9_]{3,16}$";
        Pattern pattern = Pattern.compile(MINECRAFT_USERNAME_REGEX);
        Matcher matcher = pattern.matcher(username);
        return matcher.matches();
    }
}
