package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableLodestoneLocation;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.LodestoneTracker;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class LodestoneTrackerComponent extends ItemComponent {
    private final @NotNull ResolvableBoolean lodestoneTracked;
    private final @Nullable ResolvableLodestoneLocation lodestoneLocation;

    public LodestoneTrackerComponent(@NotNull ResolvableBoolean lodestoneTracked, @Nullable ResolvableLodestoneLocation lodestoneLocation) {
        this.lodestoneTracked = lodestoneTracked;
        this.lodestoneLocation = lodestoneLocation;
    }

    public @NotNull ResolvableBoolean isLodestoneTracked() {
        return this.lodestoneTracked;
    }

    public @Nullable ResolvableLodestoneLocation getLodestoneLocation() {
        return this.lodestoneLocation;
    }

    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        if (!Boolean.TRUE.equals(this.lodestoneTracked.serialize())) map.put("tracked", this.lodestoneTracked.serialize());
        if (this.lodestoneLocation != null) map.put("target", this.lodestoneLocation.serialize());
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        LodestoneTracker.Builder lodestoneTracker = LodestoneTracker.lodestoneTracker();

        this.applyResolvable(context, lodestoneTracker::tracked, this.lodestoneTracked);
        Resolvable.applyResolvable(context, this.lodestoneLocation, lodestoneTracker::location);

        itemStack.setData(DataComponentTypes.LODESTONE_TRACKER, lodestoneTracker.build());
    }
}
