package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.DyeColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ShieldMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public record BaseColorComponent(
       @NotNull DyeColor baseColor
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, ShieldMeta.class, shieldMeta -> shieldMeta.setBaseColor(baseColor));
        if (!apply){
            if (Configuration.enableDebug)
                Logger.info("Failed to apply BaseColor to ItemStack of type "+itemStack.getType().name()+" check if it's a shield.");
        }
    }
}
