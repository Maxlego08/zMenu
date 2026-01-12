package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CurrencyWithdrawAction;
import fr.traqueur.currencies.Currencies;
import org.jspecify.annotations.NonNull;

import java.io.File;

public class CurrencyWithdrawLoader extends ActionLoader {

    public CurrencyWithdrawLoader() {
        super("withdraw", "money remove");
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        String bigDecimal = accessor.getString("amount");
        Currencies currencies = Currencies.valueOf(accessor.getString("currency", Currencies.VAULT.name()).toUpperCase());
        String economyName = accessor.getString("economy", null);
        String reason = accessor.getString("reason", "no reason");
        return new CurrencyWithdrawAction(bigDecimal, currencies, economyName, reason);
    }
}
