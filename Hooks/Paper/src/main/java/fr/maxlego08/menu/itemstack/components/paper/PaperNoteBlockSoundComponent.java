package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import io.papermc.paper.datacomponent.DataComponentTypes;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperNoteBlockSoundComponent extends ItemComponent {
    private final ResolvableNamespacedKey resolvableNamespacedKey;

    public PaperNoteBlockSoundComponent(@Nullable ResolvableNamespacedKey resolvableNamespacedKey) {
        this.resolvableNamespacedKey = resolvableNamespacedKey;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.resolvableNamespacedKey, resolved -> itemStack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, resolved));
    }
}
