package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableInt;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.OminousBottleAmplifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class OminousBottleAmplifierComponent extends ItemComponent {
    private final ResolvableInt amplifier;

    public OminousBottleAmplifierComponent(int amplifier) {
        this.amplifier = ResolvableInt.of(amplifier);
    }

    public OminousBottleAmplifierComponent(ResolvableInt amplifier) {
        this.amplifier = amplifier;
    }

    public ResolvableInt getAmplifier() {
        return this.amplifier;
    }

    @Override
    public @Nullable Object serialize() {
        return this.amplifier.serialize();
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        this.applyResolvable(context, amplifier -> itemStack.setData(DataComponentTypes.OMINOUS_BOTTLE_AMPLIFIER, OminousBottleAmplifier.amplifier(amplifier)), this.amplifier);
    }
}
