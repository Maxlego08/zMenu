package fr.maxlego08.menu.loader.permissible;

import fr.maxlego08.menu.api.ButtonManager;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.loader.ZPermissibleLoader;
import fr.maxlego08.menu.requirement.permissible.ZCurrencyPermissible;
import fr.traqueur.currencies.Currencies;

import java.io.File;
import java.math.BigDecimal;
import java.util.List;

public class CurrencyPermissibleLoader extends ZPermissibleLoader {

    private final ButtonManager buttonManager;

    public CurrencyPermissibleLoader(ButtonManager buttonManager) {
        this.buttonManager = buttonManager;
    }

    @Override
    public String getKey() {
        return "money";
    }

    @Override
    public Permissible load(String path, TypedMapAccessor accessor, File file) {
        List<Action> denyActions = loadAction(buttonManager, accessor, "deny", path, file);
        List<Action> successActions = loadAction(buttonManager, accessor, "success", path, file);
        String amount = accessor.getString("amount");
        Currencies currencies = Currencies.valueOf(accessor.getString("currency", Currencies.VAULT.name()));
        String economyName = accessor.getString("economy", null);
        return new ZCurrencyPermissible(denyActions, successActions, currencies, amount, economyName);
    }
}
