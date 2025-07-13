package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuVersion extends VCommand {

    public CommandMenuVersion(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_VERSION);
        this.addSubCommand("version", "v", "ver");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        message(plugin, sender, "§aVersion du plugin§7: §2" + plugin.getDescription().getVersion());
        message(plugin, sender, "§aAuteur§7: §2Maxlego08");
        message(plugin, sender, "§aMarketplace/Inventory builder§7: §2https://minecraft-inventory-builder.com/");
        message(plugin, sender, "§aDiscord§7: §2https://discord.groupez.dev/");
        message(plugin, sender, "§aDownload here§7: §2https://groupez.dev/resources/253");
        message(plugin, sender, "§aSponsor§7: §chttps://serveur-minecraft-vote.fr/?ref=345");

        return CommandType.SUCCESS;
    }

}
