package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class DamageComponent extends ItemComponent {

    private final int damage;

    public DamageComponent(int damage) {
        this.damage = damage;
    }

    public int getDamage() {
        return this.damage;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, Damageable.class, damageable -> {
            damageable.setDamage(this.damage);
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Failed to apply DamageComponent to itemStack: " + itemStack.getType().name() + ". This item does not support damageable meta.");
        }
    }

}
