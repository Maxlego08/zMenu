package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;

import java.math.BigDecimal;

public class CurrencyWithdrawAction extends Action {

    private final String amount;
    private final Currencies currencies;
    private final String economyName;

    public CurrencyWithdrawAction(String amount, Currencies currencies, String economyName) {
        this.amount = amount;
        this.currencies = currencies;
        this.economyName = economyName;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        this.currencies.withdraw(player, new BigDecimal(papi(placeholders.parse(this.amount), player, false)), this.economyName == null ? currencies.name() : this.economyName);
    }
}
