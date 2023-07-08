package fr.maxlego08.menu.button;

import fr.maxlego08.menu.action.permissible.ZPlaceholderPermissible;
import fr.maxlego08.menu.api.action.permissible.PlaceholderPermissible;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.enums.PlaceholderAction;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;

public abstract class ZPlaceholderButton extends ZPermissibleButton implements PlaceholderButton {

    private PlaceholderAction action;
    private String placeholder;
    private String value;

    @Override
    public String getPlaceHolder() {
        return this.placeholder;
    }

    @Override
    public PlaceholderAction getAction() {
        return this.action;
    }

    /**
     * @param action the action to set
     */
    public ZPlaceholderButton setAction(PlaceholderAction action) {
        this.action = action;
        return this;
    }

    @Override
    public boolean hasPlaceHolder() {
        return this.placeholder != null && this.action != null;
    }

    @Override
    public boolean hasPermission() {
        return super.hasPermission() || this.hasPlaceHolder();
    }

    @Override
    public String getValue() {
        return this.value;
    }

    /**
     * @param value the value to set
     */
    public ZPlaceholderButton setValue(String value) {
        this.value = value;
        return this;
    }

    @Override
    public boolean checkPermission(Player player, InventoryDefault inventory) {
        if (!this.hasPlaceHolder()) {

            return super.checkPermission(player, inventory);

        } else {

            // First check if player has permission
            boolean hasPermission = super.checkPermission(player, inventory);
            if (!hasPermission) {
                return false;
            }

            PlaceholderPermissible permissible = new ZPlaceholderPermissible(this.action, this.placeholder, this.value);
            return permissible.hasPermission(player);
        }
    }

    /**
     * @param placeholder the placeholder to set
     */
    public ZPlaceholderButton setPlaceholder(String placeholder) {
        this.placeholder = placeholder;
        return this;
    }

}
