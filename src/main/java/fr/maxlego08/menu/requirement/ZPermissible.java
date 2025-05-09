package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.zcore.utils.ZUtils;

import java.util.List;

public abstract class ZPermissible extends ZUtils implements Permissible {

    private final List<Action> denyActions;
    private final List<Action> successActions;

    public ZPermissible(List<Action> denyActions, List<Action> successActions) {
        this.denyActions = denyActions;
        this.successActions = successActions;
    }

    @Override
    public List<Action> getSuccessActions() {
        return this.successActions;
    }

    @Override
    public List<Action> getDenyActions() {
        return this.denyActions;
    }
}
