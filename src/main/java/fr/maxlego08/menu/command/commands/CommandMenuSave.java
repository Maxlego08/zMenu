package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandMenuSave extends VCommand {

    public CommandMenuSave(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("save");
        this.addRequireArg("item name");
        this.addRequireArg("type", (a, b) -> Arrays.asList("yml", "base64"));
        this.setDescription(Message.DESCRIPTION_SAVE);
        this.setPermission(Permission.ZMENU_SAVE);
        this.setConsoleCanUse(false);
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        InventoryManager inventoryManager = plugin.getInventoryManager();
        String name = this.argAsString(0);
        String type = this.argAsString(1);

        ItemStack itemStack = this.player.getItemInHand();
        if (itemStack == null || itemStack.getType() == Material.AIR) {
            message(plugin, this.sender, Message.SAVE_ERROR_EMPTY);
            return CommandType.DEFAULT;
        }

        inventoryManager.saveItem(sender, itemStack, name, type);

        return CommandType.SUCCESS;
    }

}
