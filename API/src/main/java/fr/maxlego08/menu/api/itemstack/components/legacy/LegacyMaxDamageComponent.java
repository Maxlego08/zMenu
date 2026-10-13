package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.MaxDamageComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyMaxDamageComponent extends MaxDamageComponent {

    public LegacyMaxDamageComponent(ResolvableInt maxDamage) {
        super(maxDamage);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, Damageable.class, damageable -> {
            this.applyResolvable(context, damageable::setMaxDamage, this.getMaxDamage());
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply MaxDamageComponent to item: " + itemStack.getType().name());
    }
}
