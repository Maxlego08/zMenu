package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.traqueur.currencies.Currencies;

import java.util.List;

public abstract class CurrencyPermissible extends Permissible {

    public CurrencyPermissible(List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
    }

    public abstract String getAmount();

    public abstract Currencies getCurrency();

}
