package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class BaseColorComponent extends ItemComponent {
    private final @NotNull DyeColor baseColor;

    public BaseColorComponent(@NotNull DyeColor baseColor) {
        this.baseColor = baseColor;
    }

    public @NotNull DyeColor getBaseColor() {
        return this.baseColor;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, ShieldMeta.class, shieldMeta -> shieldMeta.setBaseColor(baseColor));
        if (!apply && Configuration.enableDebug){
            Logger.info("Failed to apply BaseColor to ItemStack of type "+itemStack.getType().name()+" check if it's a shield.");
        }
    }
}
