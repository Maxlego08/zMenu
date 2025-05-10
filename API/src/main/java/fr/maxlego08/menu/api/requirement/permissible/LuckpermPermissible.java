package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;

import java.util.List;

public abstract class LuckpermPermissible extends Permissible {

    public LuckpermPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    public abstract String getGroupName();

}
