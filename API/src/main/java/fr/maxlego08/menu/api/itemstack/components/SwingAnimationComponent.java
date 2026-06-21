package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SwingAnimationComponent extends ItemComponent {
    private final @NotNull ResolvableInt duration;
    private final @NotNull ResolvableEnum<org.bukkit.inventory.meta.components.SwingAnimationComponent.Type> type;

    public SwingAnimationComponent(@NotNull ResolvableInt duration, @NotNull ResolvableEnum<org.bukkit.inventory.meta.components.SwingAnimationComponent.Type> type) {
        this.duration = duration;
        this.type = type;
    }

    public @NotNull ResolvableInt getDuration() {
        return this.duration;
    }

    public @NotNull Resolvable<org.bukkit.inventory.meta.components.SwingAnimationComponent.Type> getType() {
        return this.type;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        org.bukkit.inventory.meta.components.SwingAnimationComponent swingAnimation = itemMeta.getSwingAnimation();

        this.applyResolvable(context, swingAnimation::setDuration, this.duration);
        this.applyResolvable(context, swingAnimation::setType, this.type);

        itemStack.setItemMeta(itemMeta);
    }
}
