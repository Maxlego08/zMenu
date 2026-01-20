package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.MusicInstrument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class InstrumentComponent extends ItemComponent {
    private final @NotNull MusicInstrument instrument;

    public InstrumentComponent(@NotNull MusicInstrument instrument) {
        this.instrument = instrument;
    }

    public @NotNull MusicInstrument getInstrument() {
        return this.instrument;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, MusicInstrumentMeta.class, musicInstrumentMeta -> {
            musicInstrumentMeta.setInstrument(this.instrument);
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply InstrumentComponent to itemStack: " + itemStack.getType().name());
    }
}
