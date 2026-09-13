package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.ActionResult;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.traqueur.currencies.Currencies;
import fr.traqueur.currencies.TransactionResult;
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
        this.withdraw(player, placeholders);
    }

    /**
     * Takes the money and stops the actions after this one when it could not be taken.
     *
     * <p>This is the point of the action: a shop button is configured as "take the money" followed
     * by "give the reward", and the reward must not be handed over when the payment failed.</p>
     */
    @Override
    protected ActionResult executeChain(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {

        TransactionResult result = this.withdraw(player, placeholders);
        if (result == null) {
            this.send(inventory, player, Message.CURRENCY_ERROR);
            return ActionResult.STOP;
        }

        switch (result.getStatus()) {
            case SUCCESS:
                return ActionResult.CONTINUE;

            case INSUFFICIENT_FUNDS:
                this.send(inventory, player, Message.CURRENCY_NOT_ENOUGH);
                return ActionResult.STOP;

            case UNSUPPORTED:
                Logger.info("The currency " + this.currencies.name() + " cannot take money safely: "
                        + result.getErrorMessage() + " Nothing was taken and the following actions were skipped.",
                        Logger.LogType.ERROR);
                this.send(inventory, player, Message.CURRENCY_ERROR);
                return ActionResult.STOP;

            default:
                Logger.info("Could not take " + result.getAmount() + " " + this.currencies.name()
                        + " from " + player.getName() + ": " + result.getErrorMessage(), Logger.LogType.ERROR);
                this.send(inventory, player, Message.CURRENCY_ERROR);
                return ActionResult.STOP;
        }
    }

    private void send(InventoryEngine inventory, Player player, Message message) {
        inventory.getPlugin().getMetaUpdater().sendMessage(player, message.getMessage());
    }

    /**
     * @return The outcome of the withdrawal, or null when the configured amount is not a number.
     */
    private TransactionResult withdraw(Player player, Placeholders placeholders) {

        BigDecimal parsedAmount;
        try {
            parsedAmount = new BigDecimal(this.papi(placeholders.parse(this.amount), player));
        } catch (NumberFormatException exception) {
            Logger.info("The amount \"" + this.amount + "\" of a currency withdraw action is not a valid number.", Logger.LogType.ERROR);
            return null;
        }

        return this.currencies.withdrawIfSufficient(player.getUniqueId(), parsedAmount,
                this.economyName == null ? "default" : this.economyName,
                this.papi(placeholders.parse(this.reason), player));
    }
}
