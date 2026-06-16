package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.regex.Pattern;

public abstract class RegexPermissible extends Permissible {

    public RegexPermissible(@NotNull List<Action> denyActions,@NotNull List<Action> successActions) {
        super(denyActions, successActions);
    }

    @Nullable
    public abstract Pattern getPattern();

    @Nullable
    public abstract String getPlaceholder();

}
