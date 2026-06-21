package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class UnbreakableComponent extends ItemComponent {
    private final ResolvableBoolean unbreakable;

    public UnbreakableComponent(boolean unbreakable) {
        this.unbreakable = ResolvableBoolean.of(unbreakable);
    }

    public UnbreakableComponent(ResolvableBoolean unbreakable) {
        this.unbreakable = unbreakable;
    }

    public ResolvableBoolean getUnbreakable() {
        return this.unbreakable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            Resolvable.applyResolvable(context, this.unbreakable, itemMeta::setUnbreakable);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
