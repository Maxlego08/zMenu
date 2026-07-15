package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.ItemManager;
import fr.maxlego08.menu.api.command.CommandManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.cache.YamlFileCache;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class CommandMenuReload extends SubCommand<ZMenuPlugin> {

    public CommandMenuReload(ZMenuPlugin plugin) {
        super(plugin, "reload", "rl");
        this.setPermission(Permission.ZMENU_RELOAD.getPermission());
        this.addSubCommand(new CommandMenuReloadCommand(plugin));
        this.addSubCommand(new CommandMenuReloadInventory(plugin));
        this.addSubCommand(new CommandMenuReloadConfig(plugin));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        InventoryManager inventoryManager = this.plugin.getInventoryManager();

        YamlFileCache.clearCache();

        this.plugin.loadGlobalPlaceholders();
        this.plugin.getMessageLoader().load();
        this.plugin.reloadConfig();
        Configuration config = Configuration.getInstance();
        config.save(this.plugin.getConfig(), this.plugin.getConfigFile());
        config.load(this.plugin.getConfig());

        this.plugin.getVInventoryManager().close();
        PatternManager patternManager = this.plugin.getPatternManager();
        patternManager.loadActionsPatterns();
        patternManager.loadPatterns();

        inventoryManager.deleteInventories(this.plugin);
        inventoryManager.loadInventories();

        CommandManager commandManager = this.plugin.getCommandManager();
        commandManager.loadCommands();

        ItemManager itemManager = this.plugin.getItemManager();
        itemManager.reloadCustomItems();
        Collection<? extends Player> onlinePlayers = this.plugin.getServer().getOnlinePlayers();
        this.plugin.getScheduler().runAsync(w->{
            for (Player player : onlinePlayers) {
                itemManager.executeCheckInventoryItems(player);
            }
        });

        this.plugin.getDataManager().loadDefaultValues();

        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD, "%inventories%", inventoryManager.getInventories(this.plugin).size());

        return CommandResultType.SUCCESS;
    }
}
