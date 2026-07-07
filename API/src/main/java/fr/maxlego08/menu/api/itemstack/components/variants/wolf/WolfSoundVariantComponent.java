package fr.maxlego08.menu.api.itemstack.components.variants.wolf;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class WolfSoundVariantComponent extends ItemComponent {
    private final ResolvableRegistryEntry<Wolf.SoundVariant> soundVariant;

    public WolfSoundVariantComponent(ResolvableRegistryEntry<Wolf.SoundVariant> soundVariant) {
        this.soundVariant = soundVariant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.soundVariant, resolvedSoundVariant -> itemStack.setData(DataComponentTypes.WOLF_SOUND_VARIANT, resolvedSoundVariant));
    }
}
