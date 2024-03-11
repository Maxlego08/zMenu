package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.LuckpermPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.entity.Player;

import java.util.List;

public class ZLuckPermPermissible extends ZPermissible implements LuckpermPermissible {

    private final String groupName;

    public ZLuckPermPermissible(List<Action> denyActions, List<Action> successActions, String groupName) {
        super(denyActions, successActions);
        this.groupName = groupName;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        LuckPerms api = LuckPermsProvider.get();
        User user = api.getUserManager().getUser(player.getUniqueId());
        if (user == null) return false;
        String primaryGroup = user.getPrimaryGroup();
        return primaryGroup.equals(this.groupName);
    }

    @Override
    public boolean isValid() {
        return this.groupName != null;
    }

    @Override
    public String getGroupName() {
        return this.groupName;
    }
}
