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
public class MaxDamageComponent extends ItemComponent {

    private final int maxDamage;

    public MaxDamageComponent(int maxDamage) {
        this.maxDamage = maxDamage;
    }

    public int getMaxDamage() {
        return this.maxDamage;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, Damageable.class, damageable -> damageable.setMaxDamage(this.maxDamage));
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MaxDamageComponent to item: " + itemStack.getType().name());
    }

}
