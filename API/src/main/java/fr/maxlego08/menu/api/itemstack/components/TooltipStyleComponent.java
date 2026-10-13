package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
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
    public @Nullable Object serialize() {
        return this.tooltipStyle == null ? null : this.tooltipStyle.serialize();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, tooltipStyle -> itemStack.setData(DataComponentTypes.TOOLTIP_STYLE, tooltipStyle), this.tooltipStyle);
    }
}
