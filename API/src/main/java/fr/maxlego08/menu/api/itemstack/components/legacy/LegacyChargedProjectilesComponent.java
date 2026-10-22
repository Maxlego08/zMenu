package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.ChargedProjectilesComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class LegacyChargedProjectilesComponent extends ChargedProjectilesComponent {

    public LegacyChargedProjectilesComponent(@NotNull List<@NotNull MenuItemStack> projectiles) {
        super(projectiles);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CrossbowMeta.class, crossbowMeta -> {
            for (MenuItemStack menuItemStack : this.getProjectiles()) {
                crossbowMeta.addChargedProjectile(menuItemStack.build(player));
            }
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Failed to apply ChargedProjectilesComponent to itemStack: " + itemStack.getType().name() + ". This item is probably not a crossbow.");
        }
    }
}
