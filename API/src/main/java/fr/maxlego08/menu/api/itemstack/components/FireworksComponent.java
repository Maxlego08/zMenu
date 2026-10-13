package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableFireworkEffect;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.Fireworks;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
        Fireworks.Builder fireworks = Fireworks.fireworks();

        this.applyResolvable(context, fireworks::flightDuration, this.power);
        Resolvable.applyResolvable(context, this.effects, fireworks::addEffects);

        itemStack.setData(DataComponentTypes.FIREWORKS, fireworks.build());
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (this.power.isDynamic() || !Integer.valueOf(1).equals(this.power.getResolvedValue())) {
            map.put("flight-duration", this.power.serialize());
        }
        if (!this.effects.isEmpty()) map.put("explosions", Resolvable.serializeList(this.effects));
        return map;
    }
}
