package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record UseEffectsComponent(
    boolean canSprint,
    float speedMultiplier,
    boolean interactVibration
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.UseEffectsComponent useEffects = itemMeta.getUseEffects();

            useEffects.setCanSprint(this.canSprint);
            useEffects.setSpeedMultiplier(this.speedMultiplier);
            useEffects.setInteractVibrations(this.interactVibration);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
