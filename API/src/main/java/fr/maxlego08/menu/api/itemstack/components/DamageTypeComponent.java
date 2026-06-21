package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableDamageType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageTypeComponent extends ItemComponent {

    private final @NotNull ResolvableDamageType damageType;

    public DamageTypeComponent(@NotNull ResolvableDamageType damageType) {
        this.damageType = damageType;
    }

    public @NotNull ResolvableDamageType getDamageType() {
        return this.damageType;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setDamageType, this.damageType);

        itemStack.setItemMeta(itemMeta);
    }

}
