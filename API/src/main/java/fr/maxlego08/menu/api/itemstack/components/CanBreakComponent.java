package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableBlockPredicate;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemAdventurePredicate;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CanBreakComponent extends ItemComponent {
    private final List<ResolvableBlockPredicate> blockPredicates;

    public CanBreakComponent(List<ResolvableBlockPredicate> blockPredicates) {
        this.blockPredicates = blockPredicates;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemAdventurePredicate.Builder builder = ItemAdventurePredicate.itemAdventurePredicate();

        Resolvable.applyResolvable(context, this.blockPredicates, builder::addPredicates);

        itemStack.setData(DataComponentTypes.CAN_BREAK, builder.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("blocks", Resolvable.serializeList(this.blockPredicates));
        return map;
    }
}
