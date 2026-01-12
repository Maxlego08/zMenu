package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class PlayerNamePermissible extends Permissible {

    public PlayerNamePermissible(@NotNull List<Action> denyActions,@NotNull List<Action> successActions) {
        super(denyActions, successActions);
    }

    @Nullable
    public abstract String getPlayerName();

}
