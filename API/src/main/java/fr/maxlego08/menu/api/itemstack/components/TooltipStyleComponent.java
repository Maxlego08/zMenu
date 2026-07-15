package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class TooltipStyleComponent extends ItemComponent {
    private final @Nullable ResolvableNamespacedKey tooltipStyle;

    public TooltipStyleComponent(@Nullable ResolvableNamespacedKey tooltipStyle) {
        this.tooltipStyle = tooltipStyle;
    }

    public @Nullable ResolvableNamespacedKey getTooltipStyle() {
        return this.tooltipStyle;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            Resolvable.applyResolvable(context, this.tooltipStyle, itemMeta::setTooltipStyle);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
