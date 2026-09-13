package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SulfurCubeContent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class SulfurCubeContentComponent extends ItemComponent {

    private final @NotNull MenuItemStack menuItemStack;

    public SulfurCubeContentComponent(@NotNull MenuItemStack menuItemStack) {
        this.menuItemStack = menuItemStack;
    }

    public @NotNull MenuItemStack getMenuItemStack() {
        return this.menuItemStack;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        SulfurCubeContent content = SulfurCubeContent.sulfurCubeContent(this.menuItemStack.build(player));
        itemStack.setData(DataComponentTypes.SULFUR_CUBE_CONTENT, content);
    }
}
