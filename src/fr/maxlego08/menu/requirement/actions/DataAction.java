package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.entity.Player;

public class DataAction implements Action {

    private final ActionPlayerData playerData;
    private final MenuPlugin plugin;

    public DataAction(ActionPlayerData playerData, MenuPlugin plugin) {
        this.playerData = playerData;
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player) {
        this.playerData.execute(player, this.plugin.getDataManager());
    }
}
