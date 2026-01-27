package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.FireworkEffect;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FireworkExplosionComponent extends ItemComponent {
    private final @NotNull FireworkEffect effect;

    public FireworkExplosionComponent(@NotNull FireworkEffect effect) {
        this.effect = effect;
    }

    public @NotNull FireworkEffect getEffect() {
        return this.effect;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, FireworkMeta.class, fireworkMeta -> {
            fireworkMeta.addEffect(this.effect);
        });
        if (!apply && Configuration.enableDebug) {
            Logger.info("Could not apply FireworkExplosionComponent to itemStack: " + itemStack.getType().name());
        }
    }
}
