package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class ItemGiveAction extends ActionHelper {

    private final MenuItemStack menuItemStack;

    public ItemGiveAction(MenuItemStack menuItemStack) {
        this.menuItemStack = menuItemStack;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        ItemStack itemStack = this.menuItemStack.build(player);

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(leftover -> {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            });
        }
    }
}
