package fr.maxlego08.menu.api.variable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

/**
 * Who a variable belongs to.
 */
@ApiStatus.Experimental
public enum VariableScope {

    /**
     * One value per player, owned by the player's UUID. The player data of {@code /zm players}.
     */
    PLAYER,

    /**
     * One value per server, owned by the {@code server-id} of {@code config.yml}.
     */
    SERVER,

    /**
     * One value shared by every server using the same database, owned by {@link VariableRef#GLOBAL_OWNER}.
     */
    GLOBAL;

    /**
     * @return the scope named by a text such as {@code global}, or null
     */
    public static @Nullable VariableScope parse(@Nullable String name) {
        if (name == null) return null;
        try {
            return valueOf(name.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }

    public @NotNull String id() {
        return this.name().toLowerCase(Locale.ROOT);
    }
}
