package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.FoodComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyFoodComponent extends FoodComponent {

    public LegacyFoodComponent(ResolvableInt nutrition, ResolvableFloat saturation, ResolvableBoolean canAlwaysEat) {
        super(nutrition, saturation, canAlwaysEat);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.FoodComponent food = itemMeta.getFood();
        this.applyResolvable(context, food::setNutrition, this.getNutrition());
        this.applyResolvable(context, food::setSaturation, this.getSaturation());
        this.applyResolvable(context, food::setCanAlwaysEat, this.getCanAlwaysEat());
        itemMeta.setFood(food);

        itemStack.setItemMeta(itemMeta);
    }
}
