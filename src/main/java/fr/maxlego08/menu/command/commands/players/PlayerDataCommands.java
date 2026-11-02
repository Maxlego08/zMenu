package fr.maxlego08.menu.command.commands.players;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.utils.MessageUtils;
import fr.maxlego08.menu.zcore.logger.Logger;
import fr.robie.paperdispatch.cache.OfflinePlayerCache;
import org.bukkit.command.CommandSender;

import java.util.UUID;

/**
 * Shared by the {@code /zm players} commands.
 */
final class PlayerDataCommands {

    private PlayerDataCommands() {
    }

    /**
     * Runs an action once the data of a player is in memory, on the next tick.
     *
     * <p>Only online players are always in memory, so the commands read an offline player first.
     * Without it, adding to the value of an offline player would replace it.</p>
     */
    static void whenLoaded(ZMenuPlugin plugin, CommandSender sender, UUID targetId, Runnable action) {
        plugin.getDataManager().loadPlayer(targetId).whenComplete((playerData, throwable) -> plugin.getScheduler().runNextTick(task -> {
            if (throwable != null) {
                Logger.error("Unable to load the player data of " + targetId, throwable);
                MessageUtils.message(plugin, sender, Message.PLAYERS_DATA_LOAD_FAILED, "%player%", OfflinePlayerCache.getGlobalInstance().getName(targetId));
                return;
            }
            action.run();
        }));
    }
}
