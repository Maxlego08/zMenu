package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.RefreshSlotAction;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.List;

@AutoActionLoader
public class RefreshSlotActionLoader extends ActionLoader {

    public RefreshSlotActionLoader() {
        super("refresh_slot", "refresh-slot");
    }

    @Override
    public @Nullable Action load(@NotNull String path, @NotNull TypedMapAccessor accessor, @NotNull File file) {
        List<Integer> slots = accessor.getIntList("slots");
        boolean isInPlayerInventory = accessor.getBoolean("in-player-inventory", false);
        return new RefreshSlotAction(slots, isInPlayerInventory);
    }
}
