package fr.maxlego08.menu.api.itemstack.components.variants.pig;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PigSoundVariantComponent extends ItemComponent {
    private final ResolvableRegistryEntry<Pig.SoundVariant> soundVariant;

    public PigSoundVariantComponent(ResolvableRegistryEntry<Pig.SoundVariant> soundVariant) {
        this.soundVariant = soundVariant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.soundVariant, resolvedSoundVariant -> itemStack.setData(DataComponentTypes.PIG_SOUND_VARIANT, resolvedSoundVariant));
    }

    @Override
    public @Nullable Object serialize() {
        return this.soundVariant.serialize();
    }
}
