package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableFloat;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.UseEffects;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

@SuppressWarnings("unused")
public class UseEffectsComponent extends ItemComponent {

    private final @NotNull ResolvableBoolean canSprint;
    private final @NotNull ResolvableFloat speedMultiplier;
    private final @NotNull ResolvableBoolean interactVibration;

    public UseEffectsComponent(@NotNull ResolvableBoolean canSprint, @NotNull ResolvableFloat speedMultiplier, @NotNull ResolvableBoolean interactVibration) {
        this.canSprint = canSprint;
        this.speedMultiplier = speedMultiplier;
        this.interactVibration = interactVibration;
    }

    public @NotNull ResolvableBoolean isCanSprint() {
        return this.canSprint;
    }

    public @NotNull ResolvableFloat getSpeedMultiplier() {
        return this.speedMultiplier;
    }

    public @NotNull ResolvableBoolean isInteractVibration() {
        return this.interactVibration;
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (!Boolean.FALSE.equals(this.canSprint.serialize())) map.put("can-sprint", this.canSprint.serialize());
        if (!Objects.equals(this.speedMultiplier.serialize(), 0.2f)) map.put("speed-multiplier", this.speedMultiplier.serialize());
        if (!Boolean.TRUE.equals(this.interactVibration.serialize())) map.put("interact-vibrations", this.interactVibration.serialize());
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        UseEffects.Builder builder = UseEffects.useEffects();

        this.applyResolvable(context, builder::canSprint, this.canSprint);
        this.applyResolvable(context, builder::speedMultiplier, this.speedMultiplier);
        this.applyResolvable(context, builder::interactVibrations, this.interactVibration);

        itemStack.setData(DataComponentTypes.USE_EFFECTS, builder.build());
    }
}
