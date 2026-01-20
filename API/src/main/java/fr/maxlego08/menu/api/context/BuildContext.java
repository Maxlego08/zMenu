package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BuildContext {

    @Nullable Player getPlayer();

    boolean isUseCache();

    @NotNull Placeholders getPlaceholders();
}
