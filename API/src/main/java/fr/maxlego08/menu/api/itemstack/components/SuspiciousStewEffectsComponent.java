package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvablePotionEffect;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.SuspiciousStewEffects;
import io.papermc.paper.potion.SuspiciousEffectEntry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("unused")
public class SuspiciousStewEffectsComponent extends ItemComponent {

    private final @NotNull List<ResolvablePotionEffect> effects;

    public SuspiciousStewEffectsComponent(@NotNull List<ResolvablePotionEffect> effects) {
        this.effects = effects;
    }

    public @NotNull List<ResolvablePotionEffect> getEffects() {
        return this.effects;
    }

    @Override
    public @Nullable Object serialize() {
        return Resolvable.serializeList(this.effects);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        SuspiciousStewEffects.Builder builder = SuspiciousStewEffects.suspiciousStewEffects();

        for (ResolvablePotionEffect effect : this.effects) {
            PotionEffect resolvedEffect = Resolvable.resolve(context, effect);
            if (resolvedEffect != null) {
                builder.add(SuspiciousEffectEntry.create(resolvedEffect.getType(), resolvedEffect.getDuration()));
            }
        }

        itemStack.setData(DataComponentTypes.SUSPICIOUS_STEW_EFFECTS, builder.build());
    }

}
