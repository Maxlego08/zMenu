package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.action.permissible.Permissible;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Requirement;

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
}
