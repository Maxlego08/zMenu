package fr.maxlego08.menu.action.permissible;

import fr.maxlego08.menu.api.action.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.save.Config;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;

import java.util.Map;

public class ZPlaceholderPermissible extends ZUtils implements PlaceholderPermissible {

    private final PlaceholderAction action;
    private final String placeholder;
    private final String value;

    /**
     * @param action
     * @param placeholder
     * @param value
     */
    public ZPlaceholderPermissible(PlaceholderAction action, String placeholder, String value) {
        super();
        this.action = action;
        this.placeholder = placeholder;
        this.value = value;
    }

    public ZPlaceholderPermissible(Map<String, Object> map) {
        this(PlaceholderAction.from((String) map.get("action")), (String) map.getOrDefault("placeholder", map.get("placeHolder")), (String) map.get("value"));
    }

    @Override
    public boolean hasPermission(Player player) {

        String valueAsString = papi(this.placeholder, player);
        String resultAsString = papi(this.value, player);

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

                double value = Double.parseDouble(valueAsString);
                double currentValue = Double.parseDouble(resultAsString);

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

    @Override
    public PlaceholderAction getPlaceholderAction() {
        return this.action;
    }

    @Override
    public String getPlaceholder() {
        return this.placeholder;
    }

    @Override
    public String getValue() {
        return this.value;
    }

    @Override
    public boolean isValid() {
        // ToDo, add more information about what is invalid
        return this.value != null && this.action != null && this.placeholder != null;
    }
}
