package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Permissible;
import fr.traqueur.currencies.Currencies;

import java.math.BigDecimal;

public interface CurrencyPermissible extends Permissible {

    String getAmount();

    Currencies getCurrency();

}
