package fr.maxlego08.menu.requirement.actions;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class ConnectAction extends ZUtils implements Action {

    private final String server;
    private final Plugin plugin;

    public ConnectAction(String server, Plugin plugin) {
        this.server = server;
        this.plugin = plugin;
    }

    @Override
    public void execute(Player player) {
        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("Connect");
        out.writeUTF(server);
        player.sendPluginMessage(this.plugin, "BungeeCord", out.toByteArray());
    }
}
