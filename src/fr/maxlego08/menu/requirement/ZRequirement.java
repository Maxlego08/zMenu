package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;
import org.bukkit.entity.Player;

import java.util.List;

public class ZRequirement implements Requirement {

    private final int miniumRequirement;
    private final List<Permissible> permissibles;
    private final List<Action> denyActions;
    private final List<Action> successActions;

    public ZRequirement(int miniumRequirement, List<Permissible> permissibles, List<Action> denyActions, List<Action> successActions) {
        this.miniumRequirement = miniumRequirement;
        this.permissibles = permissibles;
        this.denyActions = denyActions;
        this.successActions = successActions;
    }

    @Override
    public int getMinimumRequirements() {
        return this.miniumRequirement;
    }

    @Override
    public List<Permissible> getRequirements() {
        return this.permissibles;
    }

    @Override
    public List<Action> getDenyActions() {
        return this.denyActions;
    }

    @Override
    public List<Action> getSuccessActions() {
        return this.successActions;
    }

    @Override
    public boolean execute(Player player) {
        long requirementSuccess = this.permissibles.stream().filter(permissible -> permissible.hasPermission(player)).count();
        boolean isSuccess = requirementSuccess >= this.miniumRequirement;

        if (isSuccess) this.successActions.forEach(action -> action.execute(player));
        else this.denyActions.forEach(action -> action.execute(player));

        return isSuccess;
    }
}
