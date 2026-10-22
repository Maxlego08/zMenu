package fr.maxlego08.menu.loader.deluxemenu;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.ActionResult;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

/**
 * Stands for a DeluxeMenus action zMenu cannot run.
 * <p>
 * It stops the actions after it: a shop line such as {@code [takeexp] 10} followed by the reward
 * must not hand out the reward when the payment could not be taken. It cannot be serialized, so a
 * menu using it is reported instead of converted.
 */
public class UnsupportedDeluxeMenusAction extends Action {

    private final String line;

    public UnsupportedDeluxeMenusAction(String line) {
        this.line = line;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        this.executeChain(player, button, inventory, placeholders);
    }

    @Override
    protected ActionResult executeChain(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Logger.info("The DeluxeMenus action \"" + this.line + "\" is not supported by zMenu, the actions after it were not run.", Logger.LogType.WARNING);
        return ActionResult.STOP;
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        throw new UnsupportedOperationException("The DeluxeMenus action \"" + this.line + "\" has no zMenu equivalent");
    }
}
