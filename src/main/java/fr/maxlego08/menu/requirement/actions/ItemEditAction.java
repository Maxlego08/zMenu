package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.context.ZBuildContext;
import fr.maxlego08.menu.common.utils.ActionHelper;
import fr.maxlego08.menu.placeholder.ItemPlaceholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemEditAction extends ActionHelper {

    private final MenuItemStack menuItemStack;
    private final String slot;

    public ItemEditAction(MenuItemStack menuItemStack, String slot) {
        this.menuItemStack = menuItemStack;
        this.slot = slot;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventory, Placeholders placeholders) {
        ItemStack item = ItemPlaceholders.getItem(player, slot);
        if (item == null) return;

        //This edit the item directly
        this.menuItemStack.build(new ZBuildContext.Builder().player(player).useCache(false).baseItemStack(item).build());
    }
}
