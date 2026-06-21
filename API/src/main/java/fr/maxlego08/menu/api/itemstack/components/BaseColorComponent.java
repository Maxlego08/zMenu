package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BaseColorComponent extends ItemComponent {
    private final @NotNull Resolvable<DyeColor> baseColorResolvable;

    public BaseColorComponent(@NotNull Resolvable<DyeColor> baseColorResolvable) {
        this.baseColorResolvable = baseColorResolvable;
    }

    public @NotNull Resolvable<DyeColor> getBaseColorResolvable() {
        return this.baseColorResolvable;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, ShieldMeta.class, shieldMeta -> {
            this.applyResolvable(context, shieldMeta::setBaseColor, this.baseColorResolvable);
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply BaseColorComponent to item: " + itemStack.getType().name() + " because it does not support shield meta.");
        }
    }
}
