package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class GliderComponent extends ItemComponent {
    private final @NotNull ResolvableBoolean glider;

    public GliderComponent(@NotNull ResolvableBoolean glider) {
        this.glider = glider;
    }

    public @NotNull ResolvableBoolean isGlider() {
        return this.glider;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setGlider, this.glider);

        itemStack.setItemMeta(itemMeta);
    }
}
