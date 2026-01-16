package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record FoodComponent(
    int nutrition,
    float saturation,
    boolean canAlwaysEat
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
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
