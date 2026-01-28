package fr.maxlego08.menu.hooks.luckperms;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.Node;
import net.luckperms.api.node.NodeBuilder;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class LuckpermAction extends Action {

    private final String permission;
    private final boolean value;
    private final long expiration;
    private final TimeUnit timeUnit;

    public LuckpermAction(String permission, boolean value, long expiration, TimeUnit timeUnit) {
        this.permission = permission;
        this.value = value;
        this.expiration = expiration;
        this.timeUnit = timeUnit;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
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

        NodeBuilder<?, ?> nodeBuilder = Node.builder(permission).value(value);
        if (this.expiration > 0){
            nodeBuilder.expiry(this.expiration, this.timeUnit);
        }
        Node node = nodeBuilder.build();
        user.data().add(node);

        luckPerms.getUserManager().saveUser(user);
    }
}
