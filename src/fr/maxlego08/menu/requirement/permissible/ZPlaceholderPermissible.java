package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * Implementation of the {@link PlaceholderPermissible} interface that checks player permissions
 * based on specified placeholder values and actions.
 */
public class ZPlaceholderPermissible extends ZPermissible implements PlaceholderPermissible {

    private final PlaceholderAction action;
    private final String placeholder;
    private final String value;
    private final String targetPlayer;

    /**
     * Constructs a ZPlaceholderPermissible with the specified placeholder action, placeholder key, and value.
     *
     * @param action      The {@link PlaceholderAction} to perform.
     * @param placeholder The placeholder key to evaluate.
     * @param value       The value associated with the placeholder.
     */
    public ZPlaceholderPermissible(PlaceholderAction action, String placeholder, String value, String targetPlayer, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.action = action;
        this.placeholder = placeholder;
        this.value = value;
        this.targetPlayer = targetPlayer;
    }

    /**
     * Checks whether the player has the necessary permission based on the specified placeholder values and actions.
     *
     * @param player       The player whose permission is being checked.
     * @param placeholders
     * @return {@code true} if the player has the necessary permission, otherwise {@code false}.
     */

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {

        String valueAsString = ((!"null".equals(this.targetPlayer) && Bukkit.getOfflinePlayer(papi(this.targetPlayer, player)).hasPlayedBefore())
                ? papi(this.placeholder, Bukkit.getOfflinePlayer(papi(this.targetPlayer, player)))
                : papi(this.placeholder, player));
        String resultAsString = (valueAsString != null) ? papi(this.value, Bukkit.getOfflinePlayer(valueAsString)) : papi(this.value, player);

        if (this.action.equals(PlaceholderAction.BOOLEAN)) {

            try {
                return Boolean.valueOf(valueAsString) == Boolean.valueOf(resultAsString);
            } catch (Exception exception) {
                return false;
            }

        } else if (this.action.isString()) {

            switch (this.action) {
                case EQUALS_STRING:
                    return valueAsString.equals(resultAsString);
                case EQUALSIGNORECASE_STRING:
                    return valueAsString.equalsIgnoreCase(resultAsString);
                case CONTAINS_STRING:
                    return valueAsString.contains(resultAsString);
                default:
                    return false;
            }

        } else {

            try {

                double value = Double.parseDouble(valueAsString.replace(",", "."));
                double currentValue = Double.parseDouble(resultAsString.replace(",", "."));

                switch (this.action) {
                    case EQUAL_TO:
                        return value == currentValue;
                    case LOWER:
                        return value < currentValue;
                    case LOWER_OR_EQUAL:
                        return value <= currentValue;
                    case SUPERIOR:
                        return value > currentValue;
                    case SUPERIOR_OR_EQUAL:
                        return value >= currentValue;
                    default:
                        return true;
                }

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
