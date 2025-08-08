package fr.maxlego08.menu.hooks.luckperms;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import org.bukkit.entity.Player;

import java.util.UUID;

public class LuckpermAction extends Action {

    private final String permission;
    private final boolean value;

    public LuckpermAction(String permission, boolean value) {
        this.permission = permission;
        this.value = value;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        this.setPermissionForPlayer(player, this.permission, this.value);
    }

    public void setPermissionForPlayer(Player player, String permission, boolean value) {
        LuckPerms luckPerms = LuckPermsProvider.get();
        UUID playerUUID = player.getUniqueId();

        User user = luckPerms.getUserManager().getUser(playerUUID);

        if (user == null) {
            luckPerms.getUserManager().loadUser(playerUUID).thenAcceptAsync(loadedUser -> assignPermission(loadedUser, permission, value));
        } else {
            assignPermission(user, permission, value);
        }
    }

    private void assignPermission(User user, String permission, boolean value) {
        LuckPerms luckPerms = LuckPermsProvider.get();

        user.data().clear(n -> n.getKey().equals(permission));

        Node node = Node.builder(permission).value(value).build();
        user.data().add(node);

        luckPerms.getUserManager().saveUser(user);
    }
}
