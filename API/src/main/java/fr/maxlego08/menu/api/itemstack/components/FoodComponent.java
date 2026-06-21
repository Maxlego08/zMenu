package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.FoodComponent food = itemMeta.getFood();

            this.applyResolvable(context, food::setNutrition, this.nutrition);

            this.applyResolvable(context, food::setSaturation, this.saturation);

            this.applyResolvable(context, food::setCanAlwaysEat, this.canAlwaysEat);
            
            itemStack.setItemMeta(itemMeta);
        }
    }

}
