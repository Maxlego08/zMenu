package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class CommandMenuOpenMainMenu extends SubCommand<ZMenuPlugin> {
    /**
     * @param plugin the plugin
     */
    public CommandMenuOpenMainMenu(ZMenuPlugin plugin) {
        super(plugin, "openMainMenu", "omm");
        this.setPermission(Permission.ZMENU_OPEN_MAIN_MENU.getPermission());

        this.addOptionalArgument("player", ArgumentTypes.player());
        this.addOptionalArgument("display-message", BoolArgumentType.bool());

    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        InventoryManager inventoryManager = commandDispatch.getPlugin().getInventoryManager();

        Player player;
        Optional<PlayerSelectorArgumentResolver> optionalPlayerSelector = commandDispatch.getOptionalArgument("player", PlayerSelectorArgumentResolver.class);
        if (optionalPlayerSelector.isPresent()) {
            PlayerSelectorArgumentResolver playerSelector = optionalPlayerSelector.get();
            try {
                player = playerSelector.resolve(commandDispatch.getSource()).getFirst();
            } catch (Exception e) {
                if (Configuration.enableDebug) {
                    Logger.info("Error while resolving player selector: " + e.getMessage());
                }
                return CommandResultType.SUCCESS;
            }
        } else {
            player = commandDispatch.getPlayer();
        }

        boolean displayMessage = commandDispatch.getOptionalArgument("display-message", Boolean.class).orElse(Configuration.enableOpenMessage);
        if (player == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ERROR_PLAYER);
            return CommandResultType.SUCCESS;
        }

        String mainMenu = Configuration.mainMenu;

        Optional<Inventory> optional = inventoryManager.getInventory(mainMenu);

        if (optional.isEmpty()) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_ERROR_INVENTORY, "%name%", mainMenu);
            return CommandResultType.SUCCESS;
        }

        if (displayMessage) {
            if (commandDispatch.getSender() == player) {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_SUCCESS, "%name%", mainMenu);
            } else {
                MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.INVENTORY_OPEN_OTHER, "%name%", mainMenu, "%player%",
                        player.getName());
            }
        }

        inventoryManager.openInventory(player, optional.get());

        return CommandResultType.SUCCESS;
    }
}
