package fr.maxlego08.menu.api.itemstack.components.variants.cat;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistryEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Cat;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class CatSoundVariantComponent extends ItemComponent {
    private final ResolvableRegistryEntry<Cat.SoundVariant> soundVariant;

    public CatSoundVariantComponent(ResolvableRegistryEntry<Cat.SoundVariant> soundVariant) {
        this.soundVariant = soundVariant;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.soundVariant, resolvedSoundVariant -> itemStack.setData(DataComponentTypes.CAT_SOUND_VARIANT, resolvedSoundVariant));
    }

    @Override
    public @Nullable Object serialize() {
        return this.soundVariant.serialize();
    }
}
