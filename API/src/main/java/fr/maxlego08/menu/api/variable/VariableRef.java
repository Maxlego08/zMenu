package fr.maxlego08.menu.api.variable;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * Identifies one variable: its scope, its owner within the scope, and its name.
 *
 * @param scope the scope
 * @param owner the player's UUID, the server id, or {@link #GLOBAL_OWNER}
 * @param name  the name, as written in menus ({@code coins}, {@code event_started}...)
 */
@ApiStatus.Experimental
public record VariableRef(@NotNull VariableScope scope, @NotNull String owner, @NotNull String name) {

    /**
     * The owner of every global variable.
     */
    public static final String GLOBAL_OWNER = "*";

    public static @NotNull VariableRef player(@NotNull UUID playerId, @NotNull String name) {
        return new VariableRef(VariableScope.PLAYER, playerId.toString(), name);
    }

    public static @NotNull VariableRef server(@NotNull String serverId, @NotNull String name) {
        return new VariableRef(VariableScope.SERVER, serverId, name);
    }

    public static @NotNull VariableRef global(@NotNull String name) {
        return new VariableRef(VariableScope.GLOBAL, GLOBAL_OWNER, name);
    }
}
