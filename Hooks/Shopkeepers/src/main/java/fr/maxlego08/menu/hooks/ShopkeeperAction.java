package fr.maxlego08.menu.hooks;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Optional;

public class ShopkeeperAction extends Action {

    private final MenuPlugin plugin;
    private final String shopName;

    public ShopkeeperAction(MenuPlugin plugin, String shopName) {
        this.plugin = plugin;
        this.shopName = shopName;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        Optional<? extends Shopkeeper> optional = ShopkeepersAPI.getShopkeeperRegistry().getShopkeepersByName(plugin.parse(player, placeholders.parse(this.shopName))).findFirst();
        if (optional.isPresent()) {
            optional.get().openTradingWindow(player);
        } else {
            plugin.getLogger().severe("ShopKeeper " + shopName + " was not found !");
            player.sendMessage("§cShopKeeper " + shopName + " was not found !");
        }
    }
}
