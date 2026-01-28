package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperTooltipDisplayComponent extends ItemComponent {
    private final TooltipDisplay tooltipDisplay;

    public PaperTooltipDisplayComponent(@NotNull TooltipDisplay tooltipDisplay) {
        this.tooltipDisplay = tooltipDisplay;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, tooltipDisplay);
    }
}
