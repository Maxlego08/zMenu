package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.NoteBlockSoundComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableNamespacedKey;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyNoteBlockSoundComponent extends NoteBlockSoundComponent {

    public LegacyNoteBlockSoundComponent(@Nullable ResolvableNamespacedKey resolvableNamespacedKey) {
        super(resolvableNamespacedKey);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, SkullMeta.class, skullMeta ->
                Resolvable.applyResolvable(context, this.getNoteBlockSound(), skullMeta::setNoteBlockSound));
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply NoteBlockSoundComponent to item: " + itemStack.getType().name());
    }
}
