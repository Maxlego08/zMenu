package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
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
    public boolean execute(Player player, Button button, InventoryDefault inventory) {

        AtomicBoolean earlyExit = new AtomicBoolean(false);
        long requirementSuccess = this.permissibles.stream().filter(permissible -> {

            if (earlyExit.get()) {
                return false;
            }

            boolean result = permissible.hasPermission(player, button, inventory);
            // Execution of actions in case of success permissible
            if (result) {
                permissible.getSuccessActions().forEach(action -> action.preExecute(player, button, inventory));
            } else { // Execution of actions in case of deny permissible
                permissible.getDenyActions().forEach(action -> action.preExecute(player, button, inventory));
            }

            if (this.permissibles.size() == this.miniumRequirement && !result) {
                earlyExit.set(true);
            }

            return result;
        }).count();

        boolean isSuccess = requirementSuccess >= this.miniumRequirement;

        // Execution of actions in case of success permissible
        if (isSuccess) {
            this.successActions.forEach(action -> action.preExecute(player, button, inventory));
        } else { // Execution of actions in case of deny permissible
            this.denyActions.forEach(action -> action.preExecute(player, button, inventory));
        }

        return isSuccess;
    }

    @Override
    public List<ClickType> getClickTypes() {
        return this.clickTypes;
    }
}
