package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.enums.ItemSource;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.rules.Rule;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class RulePermissible extends Permissible {

    public RulePermissible(@NotNull List<Action> denyActions, @NotNull List<Action> successActions) {
        super(denyActions, successActions);
    }

    @NotNull
    public abstract Rule getRule();

    @NotNull
    public abstract ItemSource getItemSource();

    public abstract int getAmount();
}
