package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.CurrencyPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public class ZCurrencyPermissible extends ZPermissible implements CurrencyPermissible {

    private final Currencies currencies;
    private final String amount;
    private final String economyName;

    public ZCurrencyPermissible(List<Action> denyActions, List<Action> successActions, Currencies currencies, String amount, String economyName) {
        super(denyActions, successActions);
        this.currencies = currencies;
        this.amount = amount;
        this.economyName = economyName;
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public Currencies getCurrency() {
        return this.currencies;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        String result = papi(placeholders.parse(this.amount), player, false);
        BigDecimal bigDecimal = new BigDecimal(result);
        return Currencies.VAULT.getBalance(player, this.economyName == null ? currencies.name() : this.economyName).compareTo(bigDecimal) >= 0;
    }

    @Override
    public boolean isValid() {
        return this.currencies != null && this.amount != null;
    }
}
