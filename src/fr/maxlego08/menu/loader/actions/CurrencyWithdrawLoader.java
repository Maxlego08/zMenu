package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CurrencyWithdrawAction;
import fr.traqueur.currencies.Currencies;

import java.io.File;
import java.math.BigDecimal;

public class CurrencyWithdrawLoader implements ActionLoader {

    @Override
    public String getKey() {
        return "withdraw,money remove";
    }

    @Override
    public Action load(String path, TypedMapAccessor accessor, File file) {
        String bigDecimal = accessor.getString("amount");
        Currencies currencies = Currencies.valueOf(accessor.getString("currency", Currencies.VAULT.name()));
        String economyName = accessor.getString("economy", null);
        return new CurrencyWithdrawAction(bigDecimal, currencies, economyName);
    }
}
