package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.command.VCommand;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.commands.CommandType;
import org.bukkit.entity.Player;

import java.util.Collection;

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

        ZUtils.clearConfigurationCache();

        plugin.loadGlobalPlaceholders();
        plugin.getMessageLoader().load();
        plugin.reloadConfig();
        Configuration config = Configuration.getInstance();
        config.save(plugin.getConfig(),plugin.getConfigFile());
        config.load(plugin.getConfig());

        plugin.getVInventoryManager().close();
        PatternManager patternManager = plugin.getPatternManager();
        patternManager.loadActionsPatterns();
        patternManager.loadPatterns();

        inventoryManager.deleteInventories(plugin);
        inventoryManager.loadInventories();

        CommandManager commandManager = plugin.getCommandManager();
        commandManager.loadCommands();

        ItemManager itemManager = plugin.getItemManager();
        itemManager.reloadCustomItems();
        Collection<? extends Player> onlinePlayers = plugin.getServer().getOnlinePlayers();
        plugin.getScheduler().runAsync(w->{
            for (Player player : onlinePlayers) {
                itemManager.executeCheckInventoryItems(player);
            }
        });

        plugin.getDataManager().loadDefaultValues();

        message(plugin, this.sender, Message.RELOAD, "%inventories%", inventoryManager.getInventories(plugin).size());

        return CommandType.SUCCESS;
    }

}
