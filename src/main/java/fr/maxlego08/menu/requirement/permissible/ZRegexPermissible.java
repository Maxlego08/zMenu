package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.regex.Pattern;

public class ZRegexPermissible extends Permissible {

    private final Pattern pattern;
    private final String placeholder;

    public ZRegexPermissible(String regex, String placeholder, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.pattern = regex == null ? null : Pattern.compile(regex);
        this.placeholder = placeholder;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        return pattern.matcher(inventory.getPlugin().parse(player, placeholders.parse(this.placeholder))).find();
    }

    @Override
    public boolean isValid() {
        if (this.pattern == null) Logger.info("Regex is null !", Logger.LogType.WARNING);
        if (this.placeholder == null) Logger.info("Input is null !", Logger.LogType.WARNING);
        return this.pattern != null && this.placeholder != null;
    }
}
