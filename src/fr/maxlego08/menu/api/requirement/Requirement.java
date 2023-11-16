package fr.maxlego08.menu.api.requirement;

import fr.maxlego08.menu.api.action.permissible.Permissible;
import org.bukkit.entity.Player;

import java.util.List;

public interface Requirement {

    int getMinimumRequirements();

    List<Permissible> getRequirements();

    List<Action> getDenyActions();

    List<Action> getSuccessActions();

    boolean execute(Player player);

}
