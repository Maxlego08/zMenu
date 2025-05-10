package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;

import java.util.List;

public abstract class JobPermissible extends Permissible {

    public JobPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    public abstract String getJobName();

}
