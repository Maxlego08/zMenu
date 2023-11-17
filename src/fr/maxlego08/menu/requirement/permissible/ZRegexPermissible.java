package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Permissible;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import jdk.jpackage.internal.Log;
import org.bukkit.entity.Player;

import java.util.regex.Pattern;

public class ZRegexPermissible extends ZUtils implements Permissible {

    private final Pattern pattern;
    private final String placeholder;

    public ZRegexPermissible(String regex, String placeholder) {
        this.pattern = regex == null ? null : Pattern.compile(regex);
        this.placeholder = placeholder;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory) {
        return pattern.matcher(papi(this.placeholder, player)).find();
    }

    @Override
    public boolean isValid() {
        if (this.pattern == null) Logger.info("Regex is null !", Logger.LogType.WARNING);
        if (this.placeholder == null) Logger.info("Input is null !", Logger.LogType.WARNING);
        return this.pattern != null && this.placeholder != null;
    }
}
