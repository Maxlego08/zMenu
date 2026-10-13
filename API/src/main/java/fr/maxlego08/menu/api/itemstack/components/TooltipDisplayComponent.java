package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TooltipDisplayComponent extends ItemComponent {
    private final ResolvableBoolean hideTooltip;
    private final List<ResolvableRegistryEntry<DataComponentType>> hiddenComponents;

    public TooltipDisplayComponent(ResolvableBoolean hideTooltip, List<ResolvableRegistryEntry<DataComponentType>> hiddenComponents) {
        this.hideTooltip = hideTooltip;
        this.hiddenComponents = hiddenComponents;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        TooltipDisplay.Builder builder = TooltipDisplay.tooltipDisplay();

        Resolvable.applyResolvable(context, this.hideTooltip, builder::hideTooltip);
        Resolvable.applyResolvable(context, this.hiddenComponents, HashSet::new, builder::hiddenComponents);

        itemStack.setData(DataComponentTypes.TOOLTIP_DISPLAY, builder.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        Object hideTooltip = this.hideTooltip.serialize();
        if (!Boolean.FALSE.equals(hideTooltip)) map.put("hide-tooltip", hideTooltip);
        if (!this.hiddenComponents.isEmpty()) map.put("hidden-components", Resolvable.serializeList(this.hiddenComponents));
        return map;
    }
}
