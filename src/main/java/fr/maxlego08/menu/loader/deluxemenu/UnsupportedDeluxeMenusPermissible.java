package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Stands for a DeluxeMenus requirement zMenu cannot check.
 * <p>
 * It always denies, so a check that cannot be evaluated never lets a player through. It cannot be
 * serialized, so a menu using it is reported instead of converted.
 */
public class UnsupportedDeluxeMenusPermissible extends Permissible {

    private final String description;

    public UnsupportedDeluxeMenusPermissible(String description, List<Action> denyActions) {
        super(denyActions, new ArrayList<>());
        this.description = description;
    }

    @Override
    public boolean hasPermission(@NonNull Player player, Button button, @NonNull InventoryEngine inventoryEngine, @NonNull Placeholders placeholders) {
        return false;
    }

    @Override
    public boolean isValid() {
        return true;
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        throw new UnsupportedOperationException("The DeluxeMenus requirement " + this.description + " has no zMenu equivalent");
    }
}
