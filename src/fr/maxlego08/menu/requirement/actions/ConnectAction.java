package fr.maxlego08.menu.requirement.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConnectAction extends Action {

    private final String server;
    private final Plugin plugin;

    public ConnectAction(String server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
    }
}
