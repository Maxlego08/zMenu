package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.loader.actions.CurrencyDepositLoader;
import fr.traqueur.currencies.Currencies;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.math.BigDecimal;
import java.util.Map;

public class CurrencyDepositAction extends ActionHelper {

    private final String amount;
    private final Currencies currencies;
    private final String economyName;
    private final String reason;

    public CurrencyDepositAction(String amount, Currencies currencies, String economyName, String reason) {
        this.amount = amount;
        this.currencies = currencies;
        this.economyName = economyName;
        this.reason = reason;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.currencies.deposit(player.getUniqueId(), new BigDecimal(this.papi(placeholders.parse(this.amount), player)), this.economyName == null ? "default" : this.economyName, this.papi(placeholders.parse(this.reason), player));
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "deposit");
        map.put("amount", this.amount);
        if (this.currencies != Currencies.VAULT) map.put("currency", this.currencies.name());
        if (this.economyName != null) map.put("economy", this.economyName);
        if (!CurrencyDepositLoader.DEFAULT_REASON.equals(this.reason)) map.put("reason", this.reason);
    }
}
