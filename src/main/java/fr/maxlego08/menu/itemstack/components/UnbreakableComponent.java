package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UnbreakableComponent extends ItemComponent {
    private final boolean unbreakable;

    public UnbreakableComponent(boolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public boolean isUnbreakable() {
        return this.unbreakable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            itemMeta.setUnbreakable(this.unbreakable);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
