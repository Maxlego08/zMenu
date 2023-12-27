package fr.maxlego08.menu.requirement.actions;

import com.nisovin.shopkeepers.api.ShopkeepersAPI;
import com.nisovin.shopkeepers.api.shopkeeper.Shopkeeper;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ShopkeeperAction extends Action {

    private final String shopName;

    public ShopkeeperAction(String shopName) {
        this.shopName = shopName;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory) {
        Optional<? extends Shopkeeper> optional = ShopkeepersAPI.getShopkeeperRegistry().getShopkeepersByName(this.shopName).findFirst();
        if (optional.isPresent()) {
            optional.get().openTradingWindow(player);
        } else {
            Logger.info("ShopKeeper " + shopName + " was not found !", Logger.LogType.ERROR);
            player.sendMessage("§cShopKeeper " + shopName + " was not found !");
        }
    }
}
