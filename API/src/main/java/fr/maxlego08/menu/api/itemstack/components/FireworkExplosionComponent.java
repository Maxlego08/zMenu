package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class FireworkExplosionComponent extends ItemComponent {
    private final @NotNull ResolvableFireworkEffect effect;

    public FireworkExplosionComponent(@NotNull ResolvableFireworkEffect effect) {
        this.effect = effect;
    }

    public @NotNull ResolvableFireworkEffect getEffect() {
        return this.effect;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.effect, effect -> itemStack.setData(DataComponentTypes.FIREWORK_EXPLOSION, effect));
    }

    @Override
    public @Nullable Object serialize() {
        return this.effect.serialize();
    }
}
