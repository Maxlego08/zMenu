package fr.maxlego08.menu.command.commands;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.animation.slot.AnimationHistory;
import fr.maxlego08.menu.command.NameSuggestions;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.enums.Permission;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.common.utils.command.NonSpaceStringArgumentType;
import fr.robie.paperdispatch.argument.OfflinePlayerArgument;
import fr.robie.paperdispatch.cache.OfflinePlayerCache;
import fr.robie.paperdispatch.command.CommandDispatch;
import fr.robie.paperdispatch.command.CommandResultType;
import fr.robie.paperdispatch.command.SubCommand;
import io.papermc.paper.command.brigadier.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Lets an animation that only plays once play again.
 *
 * <p>Without this, a menu mistakenly set to {@code play: ONCE_FOREVER} could only be undone by
 * editing the database by hand.</p>
 */
public class CommandMenuAnimationReset extends SubCommand<ZMenuPlugin> {

    public CommandMenuAnimationReset(ZMenuPlugin plugin) {
        super(plugin, "resetanimation");
        this.setPermission(Permission.ZMENU_ANIMATION_RESET.getPermission());
        this.addRequiredArgument("player", new OfflinePlayerArgument());
        this.addOptionalArgument(Commands.argument("menu", new NonSpaceStringArgumentType()).suggests((context, builder) -> {
            NameSuggestions.suggestInventories(builder, this.plugin.getInventoryManager());
            return builder.buildFuture();
        }));
    }

    @Override
    protected @NotNull CommandResultType perform(@NotNull CommandDispatch<ZMenuPlugin> commandDispatch) {
        ZMenuPlugin plugin = commandDispatch.getPlugin();

        UUID playerUniqueId = commandDispatch.getArgument("player", UUID.class);
        String menu = commandDispatch.getArgument("menu", String.class, null);
        String playerName = OfflinePlayerCache.getGlobalInstance().getName(playerUniqueId);

        if (menu == null) {
            plugin.getInventoryManager().getAnimationManager().getHistory().forget(playerUniqueId, null);
            MessageUtils.message(plugin, commandDispatch.getSender(), Message.ANIMATION_RESET_ALL, "%player%", playerName);
            return CommandResultType.SUCCESS;
        }

        plugin.getInventoryManager().getAnimationManager().getHistory().forget(playerUniqueId, historyKey(plugin, menu));
        MessageUtils.message(plugin, commandDispatch.getSender(), Message.ANIMATION_RESET_ONE, "%player%", playerName, "%menu%", menu);
        return CommandResultType.SUCCESS;
    }

    /**
     * Reads the menu as {@code /zm open} reads it, {@code zEssentials:baltop} included. A menu that is
     * not loaded anymore keeps the key of zMenu it always had.
     */
    public static String historyKey(ZMenuPlugin plugin, String menu) {
        return plugin.getInventoryManager().findInventory(menu).map(AnimationHistory::key).orElse(plugin.getName() + ":" + menu);
    }
}
