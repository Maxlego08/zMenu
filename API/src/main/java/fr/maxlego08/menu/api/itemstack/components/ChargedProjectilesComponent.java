package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CrossbowMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class ChargedProjectilesComponent extends ItemComponent {
    private final @NotNull List<@NotNull MenuItemStack> projectiles;

    public ChargedProjectilesComponent(@NotNull List<@NotNull MenuItemStack> projectiles) {
        this.projectiles = projectiles;
    }

    public @NotNull List<@NotNull MenuItemStack> getProjectiles() {
        return this.projectiles;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, CrossbowMeta.class, crossbowMeta -> {
            for (MenuItemStack menuItemStack : projectiles) {
                crossbowMeta.addChargedProjectile(menuItemStack.build(player));
            }
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Failed to apply ChargedProjectilesComponent to itemStack: " + itemStack.getType().name()+". This item is probably not a crossbow.");
        }
    }
}
