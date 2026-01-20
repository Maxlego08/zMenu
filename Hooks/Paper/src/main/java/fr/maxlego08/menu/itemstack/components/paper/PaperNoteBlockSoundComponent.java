package fr.maxlego08.menu.itemstack.components.paper;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import io.papermc.paper.datacomponent.DataComponentTypes;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaperNoteBlockSoundComponent extends ItemComponent {
    private final @NotNull Key noteBlockSound;

    public PaperNoteBlockSoundComponent(@NotNull Key noteBlockSound) {
        this.noteBlockSound = noteBlockSound;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.setData(DataComponentTypes.NOTE_BLOCK_SOUND, this.noteBlockSound);
    }
}
