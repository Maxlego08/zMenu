package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class WeaponComponent extends ItemComponent {
    private final int itemDamagePerAttack;
    private final float disableBlockingForSeconds;

    public WeaponComponent(int itemDamagePerAttack, float disableBlockingForSeconds) {
        this.itemDamagePerAttack = itemDamagePerAttack;
        this.disableBlockingForSeconds = disableBlockingForSeconds;
    }

    public int getItemDamagePerAttack() {
        return this.itemDamagePerAttack;
    }

    public float getDisableBlockingForSeconds() {
        return this.disableBlockingForSeconds;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {

            org.bukkit.inventory.meta.components.WeaponComponent weapon = itemMeta.getWeapon();

            weapon.setItemDamagePerAttack(this.itemDamagePerAttack);
            weapon.setDisableBlockingForSeconds(this.disableBlockingForSeconds);

            itemStack.setItemMeta(itemMeta);
        }
    }
}
