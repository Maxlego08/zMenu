package fr.maxlego08.menu.hooks;

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

public class LuckPermissionSet extends Action {

    private final String permission;

    public LuckPermissionSet(String permission) {
        this.permission = permission;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        this.addPermissionToPlayer(player, this.permission);
    }

    public void addPermissionToPlayer(Player player, String permission) {

        LuckPerms luckPerms = LuckPermsProvider.get();
        UUID playerUUID = player.getUniqueId();
        User user = luckPerms.getUserManager().getUser(playerUUID);

        if (user == null) {
            luckPerms.getUserManager().loadUser(playerUUID).thenAcceptAsync(loadedUser -> assignPermission(loadedUser, permission));
        } else {
            assignPermission(user, permission);
        }
    }

    private void assignPermission(User user, String permission) {

        Node node = Node.builder(permission).value(true).build();
        user.data().add(node);

        LuckPerms luckPerms = LuckPermsProvider.get();
        luckPerms.getUserManager().saveUser(user);
    }

}
