package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.permissible.CurrencyPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.util.List;

public class ZCurrencyPermissible extends Permissible implements CurrencyPermissible {

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
    public boolean hasPermission(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        String result = inventory.getPlugin().parse(player, placeholders.parse(this.amount));
        BigDecimal bigDecimal = new BigDecimal(result);
        BigDecimal amount = this.currencies.getBalance(player, this.economyName == null ? "default" : this.economyName);
        return amount.compareTo(bigDecimal) >= 0;
    }

    @Override
    public boolean isValid() {
        return this.currencies != null && this.amount != null;
    }
}
