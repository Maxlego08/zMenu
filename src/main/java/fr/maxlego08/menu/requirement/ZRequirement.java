package fr.maxlego08.menu.requirement;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.jspecify.annotations.NonNull;

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
    public @NonNull List<Permissible> getRequirements() {
        return this.permissibles;
    }

    @Override
    public @NonNull List<Action> getDenyActions() {
        return this.denyActions;
    }

    @Override
    public @NonNull List<Action> getSuccessActions() {
        return this.successActions;
    }

    @Override
    public boolean execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventoryDefault, @NonNull Placeholders placeholders) {

        AtomicBoolean earlyExit = new AtomicBoolean(false);
        int requirementSuccess = 0;
        for (Permissible permissible : this.permissibles) {
            if (earlyExit.get()) {
                break;
            }

            boolean result = permissible.hasPermission(player, button, inventoryDefault, placeholders);
            List<Action> actions = result ? permissible.getSuccessActions() : permissible.getDenyActions();
            for (Action action : actions) {
                action.preExecute(player, button, inventoryDefault, placeholders);
            }

            if (result) {
                requirementSuccess++;
            } else if (this.permissibles.size() == this.miniumRequirement) {
                earlyExit.set(true);
            }
        }

        boolean isSuccess = requirementSuccess >= this.miniumRequirement;

        List<Action> actions = isSuccess ? this.successActions : this.denyActions;
        actions.forEach(action -> action.preExecute(player, button, inventoryDefault, placeholders));

        return isSuccess;
    }

    @Override
    public @NonNull List<ClickType> getClickTypes() {
        return this.clickTypes;
    }
}
