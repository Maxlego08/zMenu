package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseRemainder;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    public @Nullable Object serialize() {
        return this.menuItemStack.serializeToMap();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.USE_REMAINDER, UseRemainder.useRemainder(this.menuItemStack.build(player)));
    }

}
