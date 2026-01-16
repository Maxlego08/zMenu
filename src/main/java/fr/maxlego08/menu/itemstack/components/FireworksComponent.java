package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public record FireworksComponent(
    int power,
    @NotNull List<FireworkEffect> effects
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, FireworkMeta.class, fireworkMeta -> {
            fireworkMeta.setPower(this.power);
            fireworkMeta.addEffects(this.effects);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply FireworksComponent to itemStack: " + itemStack.getType().name());
    }
}
