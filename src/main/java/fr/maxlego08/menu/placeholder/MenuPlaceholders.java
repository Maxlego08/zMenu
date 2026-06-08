package fr.maxlego08.menu.placeholder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.CompatibilityUtil;
import fr.maxlego08.menu.api.utils.Message;
import fr.maxlego08.menu.common.utils.ZUtils;
import fr.maxlego08.menu.common.utils.builder.TimerBuilder;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.time.LocalDateTime;
import java.time.ZoneId;

public class MenuPlaceholders extends ZUtils {

    public void register(MenuPlugin plugin) {
        fr.maxlego08.menu.api.placeholder.LocalPlaceholder placeholder = fr.maxlego08.menu.api.placeholder.LocalPlaceholder.getInstance();

        var inventoryManager = plugin.getInventoryManager();
        var paginationManager = inventoryManager.getPaginationManager();

        placeholder.register("test", (a, b) -> "&ctest");
        
        placeholder.register("player_page", (player, s) -> String.valueOf(inventoryManager.getPage(player)));
        placeholder.register("player_next_page", (player, s) -> String.valueOf(inventoryManager.getPage(player) + 1));
        placeholder.register("player_previous_page", (player, s) -> String.valueOf(inventoryManager.getPage(player) - 1));
        placeholder.register("player_max_page", (player, s) -> String.valueOf(inventoryManager.getMaxPage(player)));
        
        placeholder.register("pagination_page_", (player, contextId) -> {
            if (paginationManager == null) return "0";
            return String.valueOf(paginationManager.getPage(player.getUniqueId(), contextId));
        });
        
        placeholder.register("pagination_page_one_indexed_", (player, contextId) -> {
            if (paginationManager == null) return "0";
            return String.valueOf(paginationManager.getPage(player.getUniqueId(), contextId) + 1);
        });
        
        placeholder.register("pagination_next_page_", (player, contextId) -> {
            if (paginationManager == null) return "0";
            return String.valueOf(paginationManager.getPage(player.getUniqueId(), contextId) + 2);
        });
        
        placeholder.register("pagination_previous_page_", (player, contextId) -> {
            if (paginationManager == null) return "0";
            int currentPage = paginationManager.getPage(player.getUniqueId(), contextId);
            return String.valueOf(Math.max(0, currentPage - 1));
        });
        
        placeholder.register("pagination_max_page_", (player, contextId) -> {
            if (paginationManager == null) return "1";
            
            String actualContextId = contextId;
            int defaultMaxPage = 0;
            
            if (contextId.contains(":")) {
                String[] parts = contextId.split(":", 2);
                actualContextId = parts[0];
                try {
                    defaultMaxPage = Integer.parseInt(parts[1]);
                } catch (NumberFormatException ignored) {
                }
            }
            
            int storedMaxPage = paginationManager.getMaxPage(player.getUniqueId(), actualContextId);
            int maxPage = Math.max(storedMaxPage, defaultMaxPage);
            return String.valueOf(maxPage + 1);
        });
        
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
        placeholder.register("formatted_math_", (player, args) -> this.format(new ExpressionBuilder(plugin.parse(player, args.replace("{", "%").replace("}", "%"))).build().evaluate()));

        // Statistics
        placeholder.register("statistic_hours_played", (player, s) -> String.valueOf((player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20) / 60));
        placeholder.register("statistic_time_played", (player, s) -> TimerBuilder.getStringTime(player.getStatistic(Statistic.PLAY_ONE_MINUTE) / 20));

        // Global Placeholders
        placeholder.register("global_placeholders_",(player, args) ->{
            Object object = plugin.getGlobalPlaceholders().get(args);
            return object == null ? Message.GLOBAL_PLACEHOLDER_NOT_FOUND.getMessage() : String.valueOf(object);
        });

        placeholder.register("time_unix_timestamp", (player, args) -> String.valueOf(System.currentTimeMillis() / 1000));
        placeholder.register("time_next_day_unix_timestamp", (player, args) -> String.valueOf(LocalDateTime.now().toLocalDate().plusDays(1L).atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()));
        placeholder.register("time_today_start_unix_timestamp", (player, args) -> String.valueOf(LocalDateTime.now().toLocalDate().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant().getEpochSecond()));

        // Player inventory slots
        placeholder.register("player_empty_slots", (offlinePlayer, s) -> {
            if (!offlinePlayer.isOnline()) return "0";
            Player player = offlinePlayer.getPlayer();
            if (player == null) return "0";
            int emptySlots = 0;
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack == null || itemStack.getType() == Material.AIR) emptySlots++;
            }
            return String.valueOf(emptySlots);
        });

        placeholder.register("player_item_count_", (offlinePlayer, args) -> {
            if (!offlinePlayer.isOnline()) return "0";
            Player player = offlinePlayer.getPlayer();
            if (player == null) return "0";
            Material material = Material.matchMaterial(args.toUpperCase());
            if (material == null) return "0";
            int count = 0;
            for (ItemStack itemStack : player.getInventory().getStorageContents()) {
                if (itemStack != null && itemStack.getType() == material) count += itemStack.getAmount();
            }
            return String.valueOf(count);
        });
    }
}
