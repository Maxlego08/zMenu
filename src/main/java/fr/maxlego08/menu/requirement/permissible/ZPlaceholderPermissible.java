package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.OfflinePlayerCache;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Implementation of the {@link PlaceholderPermissible} interface that checks player permissions
 * based on specified placeholder values and actions.
 */
public class ZPlaceholderPermissible extends PlaceholderPermissible {

    private final PlaceholderAction action;
    private final String placeholder;
    private final String value;
    private final String targetPlayer;
    private final boolean enableMathExpression;

    /**
     * Constructs a ZPlaceholderPermissible with the specified placeholder action, placeholder key, and value.
     *
     * @param action      The {@link PlaceholderAction} to perform.
     * @param placeholder The placeholder key to evaluate.
     * @param value       The value associated with the placeholder.
     */
    public ZPlaceholderPermissible(PlaceholderAction action, String placeholder, String value, String targetPlayer, List<Action> denyActions, List<Action> successActions, boolean enableMathExpression) {
        super(denyActions, successActions);
        this.action = action;
        this.placeholder = placeholder;
        this.value = value;
        this.targetPlayer = targetPlayer;
        this.enableMathExpression = enableMathExpression;
    }

    /**
     * Checks whether the player has the necessary permission based on the specified placeholder values and actions.
     *
     * @param player       The player whose permission is being checked.
     * @param placeholders Placeholders
     * @return {@code true} if the player has the necessary permission, otherwise {@code false}.
     */

    @Override
    public boolean hasPermission(Player player, Button button, InventoryEngine inventoryEngine, Placeholders placeholders) {

        MenuPlugin plugin = inventoryEngine.getPlugin();
        String valueAsString;
        String resultAsString;

        if (this.targetPlayer == null || this.targetPlayer.equalsIgnoreCase("null")) {

            valueAsString = plugin.parse(player, placeholders.parse(this.placeholder));
            resultAsString = plugin.parse(player, placeholders.parse(this.value));
        } else {

            OfflinePlayer offlinePlayer = OfflinePlayerCache.get(plugin.parse(player, placeholders.parse(this.targetPlayer)));
            valueAsString = plugin.parse(offlinePlayer.hasPlayedBefore() ? offlinePlayer : player, placeholders.parse(this.placeholder));
            resultAsString = plugin.parse(offlinePlayer.hasPlayedBefore() ? offlinePlayer : player, placeholders.parse(this.value));
        }

        if (this.action.equals(PlaceholderAction.BOOLEAN)) {

            try {
                return Boolean.valueOf(valueAsString) == Boolean.valueOf(resultAsString);
            } catch (Exception exception) {
                return false;
            }

        } else if (this.action.isString()) {

            return switch (this.action) {
                case EQUALS_STRING -> valueAsString.equals(resultAsString);
                case DIFFERENT_STRING -> !valueAsString.equals(resultAsString);
                case EQUALSIGNORECASE_STRING -> valueAsString.equalsIgnoreCase(resultAsString);
                case CONTAINS_STRING -> valueAsString.contains(resultAsString);
                default -> false;
            };

        } else {

            try {

                double value;
                double currentValue;

                if (this.enableMathExpression) {
                    value = new ExpressionBuilder(valueAsString).build().evaluate();
                    currentValue = new ExpressionBuilder(resultAsString).build().evaluate();
                } else {
                    value = Double.parseDouble(valueAsString.replace(",", "."));
                    currentValue = Double.parseDouble(resultAsString.replace(",", "."));
                }

                return switch (this.action) {
                    case EQUAL_TO -> value == currentValue;
                    case LOWER -> value < currentValue;
                    case LOWER_OR_EQUAL -> value <= currentValue;
                    case SUPERIOR -> value > currentValue;
                    case SUPERIOR_OR_EQUAL -> value >= currentValue;
                    default -> true;
                };

            } catch (Exception exception) {
                if (Config.enableDebug) {
                    exception.printStackTrace();
                }
                return false;
            }

        }
    }

    /**
     * Gets the {@link PlaceholderAction} associated with this permissible.
     *
     * @return The {@link PlaceholderAction}.
     */
    @Override
    public PlaceholderAction getPlaceholderAction() {
        return this.action;
    }

    /**
     * Gets the placeholder key associated with this permissible.
     *
     * @return The placeholder key.
     */
    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    /**
     * Gets the value associated with this permissible.
     *
     * @return The value.
     */
    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * Checks whether the ZPlaceholderPermissible instance is valid.
     *
     * @return {@code true} if the instance is valid, otherwise {@code false}.
     */
    @Override
    public boolean isValid() {
        if (this.value == null) Logger.info("Value is null !", Logger.LogType.WARNING);
        if (this.action == null) Logger.info("Action is null !", Logger.LogType.WARNING);
        if (this.placeholder == null) Logger.info("Placeholder is null !", Logger.LogType.WARNING);
        return this.value != null && this.action != null && this.placeholder != null;
    }
}
