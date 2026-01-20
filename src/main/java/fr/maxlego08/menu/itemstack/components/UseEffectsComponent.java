package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UseEffectsComponent extends ItemComponent {

    private final boolean canSprint;
    private final float speedMultiplier;
    private final boolean interactVibration;

    public UseEffectsComponent(boolean canSprint, float speedMultiplier, boolean interactVibration) {
        this.canSprint = canSprint;
        this.speedMultiplier = speedMultiplier;
        this.interactVibration = interactVibration;
    }

    public boolean isCanSprint() {
        return this.canSprint;
    }

    public float getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public boolean isInteractVibration() {
        return this.interactVibration;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
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
