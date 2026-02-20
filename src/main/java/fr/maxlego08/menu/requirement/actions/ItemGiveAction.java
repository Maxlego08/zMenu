package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jspecify.annotations.NonNull;

import java.util.Map;

public class ItemGiveAction extends ActionHelper {

    private final MenuItemStack menuItemStack;

    public ItemGiveAction(MenuItemStack menuItemStack) {
        this.menuItemStack = menuItemStack;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        ItemStack itemStack = this.menuItemStack.build(player);

        Map<Integer, ItemStack> leftovers = player.getInventory().addItem(itemStack);
        if (!leftovers.isEmpty()) {
            leftovers.values().forEach(leftover -> {
                player.getWorld().dropItemNaturally(player.getLocation(), leftover);
            });
        }
    }
}
