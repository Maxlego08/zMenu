package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.command.commands.bedrock.CommandBedrock;
import fr.maxlego08.menu.command.commands.dialogs.CommandDialog;
import fr.maxlego08.menu.command.commands.players.CommandMenuPlayers;
import fr.maxlego08.menu.command.commands.reload.CommandMenuReload;
import fr.maxlego08.menu.command.commands.website.CommandMenuWebsite;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.command.BaseCommand;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class CommandMenu extends BaseCommand<ZMenuPlugin> {

    public CommandMenu(ZMenuPlugin plugin) {
        super(plugin, "zmenu", "zm");
        this.setPermission(Permission.ZMENU_USE.getPermission());
        this.addSubCommand(new CommandMenuOpen(plugin));
        this.addSubCommand(new CommandMenuList(plugin));
        this.addSubCommand(new CommandMenuCreate(plugin));
        this.addSubCommand(new CommandMenuReload(plugin));
        this.addSubCommand(new CommandMenuVersion(plugin));
        this.addSubCommand(new CommandMenuPlayers(plugin));
        this.addSubCommand(new CommandMenuOpenMainMenu(plugin));
        this.addSubCommand(new CommandMenuTestDupe(plugin));
        this.addSubCommand(new CommandMenuSave(plugin));
        this.addSubCommand(new CommandMenuGiveOpenItem(plugin));
        this.addSubCommand(new CommandMenuEditor(plugin));
        this.addSubCommand(new CommandMenuDocumentation(plugin));
        this.addSubCommand(new CommandAddons(plugin));
        this.addSubCommand(new CommandDumplog(plugin));
        this.addSubCommand(new CommandContributors(plugin));
        this.addSubCommand(new CommandMenuGiveItem(plugin));

        if (plugin.getConfig().getBoolean("DEV-ONLY-DONT-ENABLE-THIS", false)) {
            this.addSubCommand(new CommandMenuWebsite(plugin));
        }

        if (plugin.isPaperOrFolia() && MinecraftVersion.getCurrentVersion().isAtLeast(MinecraftVersion.parse("1.21.7")) && Configuration.enableMiniMessageFormat) {
            this.addSubCommand(new CommandDialog(plugin));
        }

        if (plugin.getBedrockManager() != null) {
            this.addSubCommand(new CommandBedrock(plugin));
        }
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        CommandSender sender = commandDispatch.getSender();
        MessageUtils.message(commandDispatch.getPlugin(), sender, "\"<white>Inventory Builder/Marketplace§8: <click:open_url:'https://minecraft-inventory-builder.com/'><green>https://minecraft-inventory-builder.com/</click>\"");
        return CommandResultType.SUCCESS;
    }
}
