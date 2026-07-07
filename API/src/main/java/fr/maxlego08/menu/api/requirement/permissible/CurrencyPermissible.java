package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.traqueur.currencies.Currencies;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class CurrencyPermissible extends Permissible {

    public CurrencyPermissible(@NotNull List<Action> denyActions,@NotNull List<Action> successActions) {
        super(denyActions, successActions);
    }

    @Nullable
    public abstract String getAmount();

    @Nullable
    public abstract Currencies getCurrency();

}
