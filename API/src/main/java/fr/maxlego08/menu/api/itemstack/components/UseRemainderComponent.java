package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UseRemainderComponent extends ItemComponent {

    private final @NotNull MenuItemStack menuItemStack;

    public UseRemainderComponent(@NotNull MenuItemStack menuItemStack) {
        this.menuItemStack = menuItemStack;
    }

    public @NotNull MenuItemStack getMenuItemStack() {
        return this.menuItemStack;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            itemMeta.setUseRemainder(this.menuItemStack.build(player));
            itemStack.setItemMeta(itemMeta);
        }
    }

}
