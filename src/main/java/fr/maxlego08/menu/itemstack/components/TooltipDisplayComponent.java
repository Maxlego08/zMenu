package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TooltipDisplayComponent extends ItemComponent {
    private final boolean hideTooltip;

    public TooltipDisplayComponent(boolean hideTooltip) {
        this.hideTooltip = hideTooltip;
    }

    public boolean isHideTooltip() {
        return this.hideTooltip;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            itemMeta.setHideTooltip(this.hideTooltip);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
