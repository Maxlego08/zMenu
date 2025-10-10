package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.entity.Player;

public class CommandMenuGiveItem extends VCommand {
    private final ItemManager itemManager;
    public CommandMenuGiveItem(ZMenuPlugin plugin) {
        super(plugin);
        this.itemManager = plugin.getItemManager();
        this.addSubCommand("giveitem");
        this.setPermission(Permission.ZMENU_GIVE_ITEM);
        this.addRequireArg("itemId",(sender,args)-> this.itemManager.getItemIds().stream().toList());
        this.addOptionalArg("player");

    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {
        String itemId = this.argAsString(0);
        Player target = this.argAsPlayer(1, this.player);
        if (target == null) {
            message(plugin, sender, Message.COMMAND_PLAYER_NOT_FOUND, "%player%", this.argAsString(1));
            return CommandType.DEFAULT;
        }
        if (!this.itemManager.isCustomItem(itemId)) {
            message(plugin, sender, "&cItem with ID %itemId% does not exist.", "%itemId%", itemId);
            return CommandType.DEFAULT;
        }
        this.itemManager.giveItem(target, itemId);
        if (target.equals(this.player)) {
            message(plugin, sender, "&aYou have received the item %itemId%.", "%itemId%", itemId);
        } else {
            message(plugin, sender, "&aYou have given the item %itemId% to %player%.", "%itemId%", itemId, "%player%", target.getName());
            message(plugin, target, "&aYou have received the item %itemId% from %sender%.", "%itemId%", itemId, "%sender%", sender.getName());
        }
        return CommandType.SUCCESS;
    }
}
