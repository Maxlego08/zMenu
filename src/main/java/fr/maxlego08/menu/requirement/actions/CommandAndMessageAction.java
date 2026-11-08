package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Parent of the actions that run commands then send messages, so that
 * {@link RandomCommandAndMessageAction} can run and serialize them.
 */
public abstract class CommandAndMessageAction extends ActionHelper {

    @Override
    protected abstract void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders);

    @Override
    protected abstract void serializeProperties(@NonNull Map<String, Object> map);
}
