package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;

public class CommandMenuTestDupe extends VCommand {

    public CommandMenuTestDupe(ZMenuPlugin plugin) {
        super(plugin);
        this.setPermission(Permission.ZMENU_TEST_DUPE);
        this.setDescription(Message.DESCRIPTION_TEST_DUPE);
        this.addSubCommand("testdupe");
        this.addRequireArg("type", (a, b) -> Arrays.asList("inventory", "item"));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        String arg = this.argAsString(0);

        ItemStack itemStack = new ItemStack(Material.STONE);
        itemStack = plugin.getDupeManager().protectItem(itemStack);

        if (arg.equalsIgnoreCase("inventory")) {
            player.getInventory().addItem(itemStack.clone());
        } else if (arg.equalsIgnoreCase("item")) {
            player.getWorld().dropItem(player.getLocation(), itemStack.clone());
        } else return CommandType.SYNTAX_ERROR;

        return CommandType.SUCCESS;
    }

}
