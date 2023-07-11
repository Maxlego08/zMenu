package fr.maxlego08.menu.action;

import fr.maxlego08.menu.api.action.Action;
import fr.maxlego08.menu.api.action.ActionClick;
import fr.maxlego08.menu.api.action.permissible.Permissible;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;

import java.util.List;

public class ZAction implements Action {

    private final List<ClickType> clickTypes;
    private final ActionClick allowAction;
    private final ActionClick denyAction;
    private final List<Permissible> permissibles;

    /**
     * @param clickTypes
     * @param allowAction
     * @param denyAction
     * @param permissibles
     */
    public ZAction(List<ClickType> clickTypes, ActionClick allowAction, ActionClick denyAction,
                   List<Permissible> permissibles) {
        super();
        this.clickTypes = clickTypes;
        this.allowAction = allowAction;
        this.denyAction = denyAction;
        this.permissibles = permissibles;
    }

    @Override
    public List<ClickType> getClickType() {
        return this.clickTypes;
    }

    @Override
    public ActionClick getAllowAction() {
        return this.allowAction;
    }

    @Override
    public ActionClick getDenyAction() {
        return this.denyAction;
    }

    @Override
    public List<Permissible> getPermissibles() {
        return this.permissibles;
    }

    @Override
    public void execute(Player player, ClickType type) {

        boolean hasPermission = true;
        if (this.permissibles.size() > 0) {
            hasPermission = this.permissibles.stream().allMatch(permissible -> permissible.hasPermission(player));
        }

        if (hasPermission) {

            if (this.allowAction != null) {
                this.allowAction.execute(player);
            }

        } else {

            if (this.denyAction != null) {
                this.denyAction.execute(player);
            }

        }

    }

    @Override
    public boolean isClick(ClickType type) {
        return this.clickTypes.contains(type);
    }

}
