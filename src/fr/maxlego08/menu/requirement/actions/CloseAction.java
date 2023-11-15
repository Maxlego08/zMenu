package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

public class CloseAction extends ZUtils implements Action {

    @Override
    public void execute(Player player) {
        player.closeInventory();
    }
}
