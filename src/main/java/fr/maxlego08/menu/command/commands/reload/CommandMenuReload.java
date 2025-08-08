package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.zcore.enums.Permission;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;

public class CommandMenuReload extends VCommand {

    public CommandMenuReload(ZMenuPlugin plugin) {
        super(plugin);
        this.addSubCommand("reload", "rl");
        this.setDescription(Message.DESCRIPTION_RELOAD);
        this.setPermission(Permission.ZMENU_RELOAD);
        this.addSubCommand(new CommandMenuReloadCommand(plugin));
        this.addSubCommand(new CommandMenuReloadInventory(plugin));
        this.addSubCommand(new CommandMenuReloadConfig(plugin));
    }

    @Override
    protected CommandType perform(ZMenuPlugin plugin) {

        InventoryManager inventoryManager = plugin.getInventoryManager();

        plugin.loadGlobalPlaceholders();
        plugin.getMessageLoader().load();
        plugin.reloadConfig();
        plugin.reloadFileConfiguration();

        plugin.getVInventoryManager().close();
        plugin.getPatternManager().loadPatterns();

        inventoryManager.deleteInventories(plugin);
        inventoryManager.loadInventories();

        CommandManager commandManager = plugin.getCommandManager();
        commandManager.loadCommands();

        plugin.getDataManager().loadDefaultValues();

        message(plugin, this.sender, Message.RELOAD, "%inventories%", inventoryManager.getInventories(plugin).size());

        return CommandType.SUCCESS;
    }

}
