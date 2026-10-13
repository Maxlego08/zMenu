package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.InstrumentComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableMusicInstrument;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.MusicInstrument;
import org.bukkit.Registry;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.MusicInstrumentMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyInstrumentComponent extends InstrumentComponent {

    public LegacyInstrumentComponent(@NotNull String instrument) {
        super(instrument);
    }

    public LegacyInstrumentComponent(@NotNull ResolvableMusicInstrument instrument) {
        super(instrument);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        MusicInstrument resolved = Resolvable.resolve(context, this.getInstrument());
        if (resolved == null) return;

        MusicInstrument registered = Registry.INSTRUMENT.get(resolved.getKey());
        if (registered == null) {
            if (Configuration.enableDebug)
                Logger.info("Could not apply the custom instrument " + resolved.getKey() + ", only registered instruments are supported on this version");
            return;
        }

        boolean apply = ItemUtil.editMeta(itemStack, MusicInstrumentMeta.class, musicInstrumentMeta -> musicInstrumentMeta.setInstrument(registered));
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply InstrumentComponent to item: " + itemStack.getType().name());
    }
}
