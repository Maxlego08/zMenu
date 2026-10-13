package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.FoodProperties;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class FoodComponent extends ItemComponent {

    private final ResolvableInt nutrition;
    private final ResolvableFloat saturation;
    private final ResolvableBoolean canAlwaysEat;

    public FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) {
        this.nutrition = ResolvableInt.of(nutrition);
        this.saturation = ResolvableFloat.of(saturation);
        this.canAlwaysEat = ResolvableBoolean.of(canAlwaysEat);
    }

    public FoodComponent(ResolvableInt nutrition, ResolvableFloat saturation, ResolvableBoolean canAlwaysEat) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.canAlwaysEat = canAlwaysEat;
    }

    public ResolvableInt getNutrition() {
        return this.nutrition;
    }

    public ResolvableFloat getSaturation() {
        return this.saturation;
    }

    public ResolvableBoolean getCanAlwaysEat() {
        return this.canAlwaysEat;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        FoodProperties.Builder food = FoodProperties.food();

        this.applyResolvable(context, food::nutrition, this.nutrition);
        this.applyResolvable(context, food::saturation, this.saturation);
        this.applyResolvable(context, food::canAlwaysEat, this.canAlwaysEat);

        itemStack.setData(DataComponentTypes.FOOD, food.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("nutrition", this.nutrition.serialize());
        map.put("saturation", this.saturation.serialize());
        if (this.canAlwaysEat != null && (this.canAlwaysEat.isDynamic() || !Boolean.FALSE.equals(this.canAlwaysEat.getResolvedValue()))) {
            map.put("can-always-eat", this.canAlwaysEat.serialize());
        }
        return map;
    }

}
