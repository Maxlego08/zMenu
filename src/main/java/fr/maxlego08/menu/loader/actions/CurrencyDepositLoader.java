package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CurrencyDepositAction;
import fr.traqueur.currencies.Currencies;

import java.io.File;

public class CurrencyDepositLoader extends ActionLoader {

    public CurrencyDepositLoader() {
        super("deposit", "money add");
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String bigDecimal = accessor.getString("amount");
        Currencies currencies = Currencies.valueOf(accessor.getString("currency", Currencies.VAULT.name()).toUpperCase());
        String economyName = accessor.getString("economy", null);
        String reason = accessor.getString("reason", "no reason");
        return new CurrencyDepositAction(bigDecimal, currencies, economyName, reason);
    }
}
