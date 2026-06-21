package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UseEffectsComponent extends ItemComponent {

    private final @NotNull ResolvableBoolean canSprint;
    private final @NotNull ResolvableFloat speedMultiplier;
    private final @NotNull ResolvableBoolean interactVibration;

    public UseEffectsComponent(@NotNull ResolvableBoolean canSprint, @NotNull ResolvableFloat speedMultiplier, @NotNull ResolvableBoolean interactVibration) {
        this.canSprint = canSprint;
        this.speedMultiplier = speedMultiplier;
        this.interactVibration = interactVibration;
    }

    public @NotNull ResolvableBoolean isCanSprint() {
        return this.canSprint;
    }

    public @NotNull ResolvableFloat getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public @NotNull ResolvableBoolean isInteractVibration() {
        return this.interactVibration;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.UseEffectsComponent useEffects = itemMeta.getUseEffects();

        this.applyResolvable(context, useEffects::setCanSprint, this.canSprint);
        this.applyResolvable(context, useEffects::setSpeedMultiplier, this.speedMultiplier);
        this.applyResolvable(context, useEffects::setInteractVibrations, this.interactVibration);

        itemStack.setItemMeta(itemMeta);
    }
}
