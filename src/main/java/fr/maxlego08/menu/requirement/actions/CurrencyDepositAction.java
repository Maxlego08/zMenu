package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class CurrencyDepositAction extends ActionHelper {

    private final String amount;
    private final Currencies currencies;
    private final String economyName;

    public CurrencyDepositAction(String amount, Currencies currencies, String economyName) {
        this.amount = amount;
        this.currencies = currencies;
        this.economyName = economyName;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        this.currencies.deposit(player, new BigDecimal(papi(placeholders.parse(this.amount), player)), this.economyName == null ? "default" : this.economyName);
    }
}
