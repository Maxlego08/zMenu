package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandMenuVersion extends SubCommand<ZMenuPlugin> {

    public CommandMenuVersion(ZMenuPlugin plugin) {
        super(plugin, "version", "v", "ver");
        this.setPermission(Permission.ZMENU_VERSION.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        CommandSender sender = commandDispatch.getSender();

        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aVersion du plugin§7: §2" + commandDispatch.getPlugin().getDescription().getVersion());
        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aAuteur§7: §2Maxlego08");
        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aMarketplace/Inventory builder§7: §2https://minecraft-inventory-builder.com/");
        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aDiscord§7: §2https://discord.groupez.dev/");
        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aDownload here§7: §2https://groupez.dev/resources/253");
        MessageUtils.message(commandDispatch.getPlugin(), sender, "§aSponsor§7: §chttps://serveur-minecraft-vote.fr/?ref=345");

        return CommandResultType.SUCCESS;
    }
}
