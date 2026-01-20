package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class SwingAnimationComponent extends ItemComponent {
    private final int duration;
    private final org.bukkit.inventory.meta.components.SwingAnimationComponent.Type type;

    public SwingAnimationComponent(int duration, org.bukkit.inventory.meta.components.SwingAnimationComponent.Type type) {
        this.duration = duration;
        this.type = type;
    }

    public int getDuration() {
        return this.duration;
    }

    public org.bukkit.inventory.meta.components.SwingAnimationComponent.Type getType() {
        return this.type;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.SwingAnimationComponent swingAnimation = itemMeta.getSwingAnimation();
            swingAnimation.setDuration(this.duration);
            swingAnimation.setType(this.type);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
