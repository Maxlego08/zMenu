package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableRegistry;
import fr.maxlego08.menu.api.utils.resolvable.paper.ResolvableMusicInstrument;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.MusicInstrument;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class InstrumentComponent extends ItemComponent {
    private final @Nullable ResolvableMusicInstrument instrument;

    public InstrumentComponent(@NotNull String instrument) {
        this.instrument = new ResolvableMusicInstrument(ResolvableRegistry.autoOrNull(instrument, RegistryKey.INSTRUMENT));
    }

    public InstrumentComponent(@NotNull ResolvableMusicInstrument instrument) {
        this.instrument = instrument;
    }

    public @Nullable ResolvableMusicInstrument getInstrument() {
        return this.instrument;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Resolvable.applyResolvable(context, this.instrument, musicInstrument -> itemStack.setData(DataComponentTypes.INSTRUMENT, musicInstrument));
    }
}
