package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class MinimumAttackChargeComponent extends ItemComponent {
    private final float minimumAttackCharge;

    public MinimumAttackChargeComponent(float minimumAttackCharge) {
        this.minimumAttackCharge = minimumAttackCharge;
    }

    public float getMinimumAttackCharge() {
        return this.minimumAttackCharge;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            itemMeta.setMinimumAttackCharge(this.minimumAttackCharge);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
