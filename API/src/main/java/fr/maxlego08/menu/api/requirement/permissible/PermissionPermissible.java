package fr.maxlego08.menu.api.requirement.permissible;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Represents a condition where a player is required to have a specific permission.
 */
public abstract class PermissionPermissible extends Permissible{

    public PermissionPermissible(@NotNull List<Action> denyActions,@NotNull List<Action> successActions) {
        super(denyActions, successActions);
    }

    /**
     * Gets the permission that the player must have.
     *
     * @return The required permission.
     */
    @NotNull
    public abstract String getPermission();

    /**
     * Checks if the condition is reversed, meaning the player should not have the specified permission.
     *
     * @return True if the condition is reversed; otherwise, false.
     */
    public abstract boolean isReverse();
}
