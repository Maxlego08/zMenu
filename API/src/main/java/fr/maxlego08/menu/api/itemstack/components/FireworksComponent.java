package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class FireworksComponent extends ItemComponent {
    private final @NotNull ResolvableInt power;
    private final @NotNull List<ResolvableFireworkEffect> effects;

    public FireworksComponent(@NotNull ResolvableInt power, @NotNull List<ResolvableFireworkEffect> effects) {
        this.power = power;
        this.effects = effects;
    }

    public @NotNull ResolvableInt getPower() {
        return this.power;
    }

    public @NotNull List<ResolvableFireworkEffect> getEffects() {
        return this.effects;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, FireworkMeta.class, fireworkMeta -> {

            Resolvable.applyResolvable(context, this.power, fireworkMeta::setPower);

            Resolvable.applyResolvable(context, this.effects, fireworkMeta::addEffects);

        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply FireworksComponent to itemStack: " + itemStack.getType().name());
    }
}
