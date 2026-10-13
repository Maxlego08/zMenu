package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableSwingAnimation;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * The {@code attack_animation} component: the swing animation played when the item attacks.
 * It replaces {@code swing_animation} since 26.3.
 */
public final class AttackAnimationComponent extends ItemComponent {
    private final ResolvableSwingAnimation animation;

    public AttackAnimationComponent(ResolvableSwingAnimation animation) {
        this.animation = animation;
    }

    public ResolvableSwingAnimation getAnimation() {
        return this.animation;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.animation, resolvedAnimation -> itemStack.setData(DataComponentTypes.ATTACK_ANIMATION, resolvedAnimation));
    }

    @Override
    public @Nullable Object serialize() {
        return this.animation.serialize();
    }
}
