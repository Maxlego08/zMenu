package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TooltipStyleComponent extends ItemComponent {
    private final @Nullable NamespacedKey tooltipStyle;

    public TooltipStyleComponent(@Nullable NamespacedKey tooltipStyle) {
        this.tooltipStyle = tooltipStyle;
    }

    public @Nullable NamespacedKey getTooltipStyle() {
        return this.tooltipStyle;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            itemMeta.setTooltipStyle(this.tooltipStyle);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
