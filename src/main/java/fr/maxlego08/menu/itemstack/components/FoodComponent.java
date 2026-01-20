package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FoodComponent extends ItemComponent {

    private final int nutrition;
    private final float saturation;
    private final boolean canAlwaysEat;

    public FoodComponent(int nutrition, float saturation, boolean canAlwaysEat) {
        this.nutrition = nutrition;
        this.saturation = saturation;
        this.canAlwaysEat = canAlwaysEat;
    }

    public int getNutrition() {
        return this.nutrition;
    }

    public float getSaturation() {
        return this.saturation;
    }

    public boolean isCanAlwaysEat() {
        return this.canAlwaysEat;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            org.bukkit.inventory.meta.components.FoodComponent food = itemMeta.getFood();
            food.setNutrition(this.nutrition);
            food.setSaturation(this.saturation);
            food.setCanAlwaysEat(this.canAlwaysEat);
            itemStack.setItemMeta(itemMeta);
        }
    }

}
