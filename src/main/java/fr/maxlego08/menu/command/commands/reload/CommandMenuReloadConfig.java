package fr.maxlego08.menu.command.commands.reload;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.pattern.PatternManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.jetbrains.annotations.NotNull;

public class CommandMenuReloadConfig extends SubCommand<ZMenuPlugin> {

    public CommandMenuReloadConfig(ZMenuPlugin plugin) {
        super(plugin, "config", "cfg");
        this.setPermission(Permission.ZMENU_RELOAD_CONFIG.getPermission());
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        this.plugin.getMessageLoader().load();
        this.plugin.reloadConfig();
        Configuration config = Configuration.getInstance();
        config.save(this.plugin.getConfigDocument());
        config.load(this.plugin.getConfigDocument());
        this.plugin.warnChangedFeatures();
        if (this.plugin.getInventoryHotReload() != null) this.plugin.getInventoryHotReload().update();

        PatternManager patternManager = this.plugin.getPatternManager();
        patternManager.loadActionsPatterns();
        patternManager.loadPatterns();
        this.plugin.getInventoryManager().getAnimationManager().loadAnimations();

        // Inventories are named when they are loaded, so they are loaded again when enable-inventory-paths changes
        if (Configuration.enableInventoryPaths != this.plugin.getInventoryManager().usesInventoryPaths()) {
            this.plugin.getVInventoryManager().close();
            this.plugin.getInventoryManager().loadInventories();
        }

        MessageUtils.message(this.plugin, commandDispatch.getSender(), Message.RELOAD_FILES);
        return CommandResultType.SUCCESS;
    }
}
