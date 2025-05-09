package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Message;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuVersion extends VCommand {

    public CommandMenuVersion(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_VERSION);
        this.addSubCommand("version", "v", "ver");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        message(sender, "§aVersion du plugin§7: §2" + plugin.getDescription().getVersion());
        message(sender, "§aAuteur§7: §2Maxlego08");
        message(sender, "§aMarketplace/Inventory builder§7: §2https://minecraft-inventory-builder.com/");
        message(sender, "§aDiscord§7: §2http://discord.groupez.dev/");
        message(sender, "§aDownload here§7: §2https://groupez.dev/resources/253");
        message(sender, "§aSponsor§7: §chttps://serveur-minecraft-vote.fr/?ref=345");

        return CommandType.SUCCESS;
    }

}
