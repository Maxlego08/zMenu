package fr.maxlego08.menu.api.requirement.permissible;

import fr.traqueur.currencies.Currencies;

public interface CurrencyPermissible {

    String getAmount();

    Currencies getCurrency();

}
