package fr.maxlego08.menu.command.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.robie.paperdispatch.argument.EnumArgument;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class CommandMenuSave extends SubCommand<ZMenuPlugin> {

    public CommandMenuSave(ZMenuPlugin plugin) {
        super(plugin, "save");
        this.setPermission(Permission.ZMENU_SAVE.getPermission());
        this.addRequiredArgument("item-name", StringArgumentType.string());
        this.addOptionalArgument("type", new EnumArgument<>(SaveType.class));
        this.setPlayerOnly();
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        Player senderAsPlayer = commandDispatch.getSenderAsPlayer();

        if (senderAsPlayer == null) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), "You must be a player to use this command.");
            return CommandResultType.SUCCESS;
        }

        InventoryManager inventoryManager = commandDispatch.getPlugin().getInventoryManager();
        String name = commandDispatch.getArgument("item-name", String.class);
        SaveType type = commandDispatch.getArgument("type", SaveType.class);

        ItemStack itemStack = commandDispatch.getSenderAsPlayer().getItemInHand();
        if (itemStack.getType() == Material.AIR) {
            MessageUtils.message(commandDispatch.getPlugin(), commandDispatch.getSender(), Message.SAVE_ERROR_EMPTY);
            return CommandResultType.SUCCESS;
        }

        inventoryManager.saveItem(commandDispatch.getSender(), itemStack, name, type.name().toLowerCase(Locale.ROOT));
        return CommandResultType.SUCCESS;
    }

    private enum SaveType {
        YML,
        BASE64
    }
}
