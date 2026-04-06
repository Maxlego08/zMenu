package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuVersion extends VCommand {

    public CommandMenuVersion(ZMenuPlugin plugin) {
        super(plugin);
        this.setDescription(Message.DESCRIPTION_VERSION);
        this.addSubCommand("version", "v", "ver");
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        this.message(plugin, this.sender, "§aVersion du plugin§7: §2" + plugin.getDescription().getVersion());
        this.message(plugin, this.sender, "§aAuteur§7: §2Maxlego08");
        this.message(plugin, this.sender, "§aMarketplace/Inventory builder§7: §2https://minecraft-inventory-builder.com/");
        this.message(plugin, this.sender, "§aDiscord§7: §2https://discord.groupez.dev/");
        this.message(plugin, this.sender, "§aDownload here§7: §2https://groupez.dev/resources/253");
        this.message(plugin, this.sender, "§aSponsor§7: §chttps://serveur-minecraft-vote.fr/?ref=345");

        return CommandType.SUCCESS;
    }

}
