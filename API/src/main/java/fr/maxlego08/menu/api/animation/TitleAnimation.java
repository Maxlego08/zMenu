package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.event.inventory.InventoryType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface TitleAnimation {

    @Nullable
    PlayerTitleAnimation playTitleAnimation(@NotNull MenuPlugin plugin, int containerId,@NotNull InventoryType type, int size, Object... args);
}
