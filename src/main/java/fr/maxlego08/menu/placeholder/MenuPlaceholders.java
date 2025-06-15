package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;
import fr.maxlego08.menu.zcore.utils.builder.TimerBuilder;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;

public class MenuPlaceholders extends ZUtils {

    public void register(MenuPlugin plugin) {
        LocalPlaceholder placeholder = LocalPlaceholder.getInstance();

        var inventoryManager = plugin.getInventoryManager();
        placeholder.register("test", (a, b) -> "&ctest");
        placeholder.register("player_page", (player, s) -> String.valueOf(inventoryManager.getPage(player)));
        placeholder.register("player_next_page", (player, s) -> String.valueOf(inventoryManager.getPage(player) + 1));
        placeholder.register("player_previous_page", (player, s) -> String.valueOf(inventoryManager.getPage(player) - 1));
        placeholder.register("player_max_page", (player, s) -> String.valueOf(inventoryManager.getMaxPage(player)));
        placeholder.register("player_previous_inventories", (playeofflinePlayer, s) -> {
            if (playeofflinePlayer.isOnline()) {
                Player player = playeofflinePlayer.getPlayer();
                if (player == null) return "0";
                InventoryHolder inventoryHolder = CompatibilityUtil.getTopInventory(player).getHolder();
                if (inventoryHolder instanceof InventoryDefault inventoryDefault) {
                    return String.valueOf(inventoryDefault.getOldInventories().size());
                }
            }
            return "0";
        });

        placeholder.register("math_", (player, args) -> String.valueOf(new ExpressionBuilder(plugin.parse(player, args.replace("{", "%").replace("}", "%"))).build().evaluate()));
        placeholder.register("formatted_math_", (player, args) -> format(new ExpressionBuilder(plugin.parse(player, args.replace("{", "%").replace("}", "%"))).build().evaluate()));

        // Statistics
        placeholder.register("statistic_hours_played", (player, s) -> String.valueOf((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60));
        placeholder.register("statistic_time_played", (player, s) -> TimerBuilder.getStringTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20));
    }

}
