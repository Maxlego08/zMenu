package fr.maxlego08.menu.api.context;

import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface BuildContext {

    /**
     * Retrieves the player associated with this build context, if applicable.
     *
     * @return the player associated with this build context, or {@code null} if none is applicable.
     */
    @Nullable Player getPlayer();

    /**
     * Retrieves whether the build context should use cached values for placeholders.
     * If true, the build context will attempt to retrieve cached values for placeholders.
     * If false, the build context will always regenerate the placeholders.
     *
     * @return true if the build context should use cached values for placeholders, false otherwise.
     */
    boolean isUseCache();

    /**
     * Retrieves the placeholders used by the build context.
     *
     * @return The placeholders used by the build context.
     */
    @NotNull Placeholders getPlaceholders();
}
