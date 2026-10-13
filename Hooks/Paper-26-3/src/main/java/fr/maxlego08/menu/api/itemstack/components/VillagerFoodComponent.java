package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.VillagerFood;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

public final class VillagerFoodComponent extends ItemComponent {
    private final ResolvableInt nutrition;

    public VillagerFoodComponent(ResolvableInt nutrition) {
        this.nutrition = nutrition;
    }

    public ResolvableInt getNutrition() {
        return this.nutrition;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.nutrition, resolvedNutrition -> itemStack.setData(DataComponentTypes.VILLAGER_FOOD, VillagerFood.villagerFood(resolvedNutrition)));
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nutrition", this.nutrition.serialize());
        return map;
    }
}
