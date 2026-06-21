package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class MinimumAttackChargeComponent extends ItemComponent {
    private final @NotNull ResolvableFloat minimumAttackCharge;

    public MinimumAttackChargeComponent(@NotNull ResolvableFloat minimumAttackCharge) {
        this.minimumAttackCharge = minimumAttackCharge;
    }

    public @NotNull ResolvableFloat getMinimumAttackCharge() {
        return this.minimumAttackCharge;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setMinimumAttackCharge, this.minimumAttackCharge);

        itemStack.setItemMeta(itemMeta);
    }
}
