package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;

public class CurrencyWithdrawAction extends ActionHelper {

    private final String amount;
    private final Currencies currencies;
    private final String economyName;
    private final String reason;

    public CurrencyWithdrawAction(String amount, Currencies currencies, String economyName, String reason) {
        this.amount = amount;
        this.currencies = currencies;
        this.economyName = economyName;
        this.reason = reason;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.currencies.withdraw(player, new BigDecimal(papi(placeholders.parse(this.amount), player)), this.economyName == null ? "default" : this.economyName, papi(placeholders.parse(this.reason), player));
    }
}
