package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;

import java.util.List;
import java.util.regex.Pattern;

public abstract class RegexPermissible extends Permissible {

    public RegexPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    public abstract Pattern getPattern();

    public abstract String getPlaceholder();

}
