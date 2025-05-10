package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class ZRequirement implements Requirement {

    private final int miniumRequirement;
    private final List<Permissible> permissibles;
    private final List<Action> denyActions;
    private final List<Action> successActions;
    private final List<ClickType> clickTypes;

    public ZRequirement(int miniumRequirement, List<Permissible> permissibles, List<Action> denyActions, List<Action> successActions, List<ClickType> clickTypes) {
        this.miniumRequirement = miniumRequirement;
        this.permissibles = permissibles;
        this.denyActions = denyActions;
        this.successActions = successActions;
        this.clickTypes = clickTypes;
    }

    @Override
    public int getMinimumRequirements() {
        return this.miniumRequirement;
    }

    @Override
    public List<Permissible> getRequirements() {
        return this.permissibles;
    }

    @Override
    public List<Action> getDenyActions() {
        return this.denyActions;
    }

    @Override
    public List<Action> getSuccessActions() {
        return this.successActions;
    }

    @Override
    public boolean execute(Player player, Button button, InventoryEngine inventoryDefault, Placeholders placeholders) {

        AtomicBoolean earlyExit = new AtomicBoolean(false);
        long requirementSuccess = this.permissibles.stream().filter(permissible -> {

            if (earlyExit.get()) {
                return false;
            }

            boolean result = permissible.hasPermission(player, button, inventoryDefault, placeholders);
            List<Action> actions = result ? permissible.getSuccessActions() : permissible.getDenyActions();
            actions.forEach(action -> action.preExecute(player, button, inventoryDefault, placeholders));

            if (this.permissibles.size() == this.miniumRequirement && !result) {
                earlyExit.set(true);
            }

            return result;
        }).count();

        boolean isSuccess = requirementSuccess >= this.miniumRequirement;

        List<Action> actions = isSuccess ? this.successActions : this.denyActions;
        actions.forEach(action -> action.preExecute(player, button, inventoryDefault, placeholders));

        return isSuccess;
    }

    @Override
    public List<ClickType> getClickTypes() {
        return this.clickTypes;
    }
}
