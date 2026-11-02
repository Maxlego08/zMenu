package fr.maxlego08.menu.storage;

import fr.maxlego08.menu.api.storage.spi.StorageEvent;
import fr.maxlego08.menu.api.variable.VariableRef;
import fr.maxlego08.menu.api.variable.VariableScope;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Turns {@link StorageEvent}s into flat fields and back, the form every broker sends them in: a
 * table row, a document, a JSON message. The fields are {@code origin}, {@code type}
 * ({@code variable}, {@code name}, {@code owner}, {@code scope}, {@code session}), and, depending on
 * the type, {@code scope}, {@code owner} and {@code name}. Absent fields are null.
 */
public final class StorageEvents {

    private StorageEvents() {
    }

    public static @NotNull Map<String, String> toFields(@NotNull StorageEvent event) {
        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("origin", event.origin());
        String scope = null;
        String owner = null;
        String name = null;
        String type;
        switch (event) {
            case StorageEvent.VariableChanged changed -> {
                type = "variable";
                scope = changed.ref().scope().id();
                owner = changed.ref().owner();
                name = changed.ref().name();
            }
            case StorageEvent.NameCleared cleared -> {
                type = "name";
                scope = cleared.scope().id();
                name = cleared.name();
            }
            case StorageEvent.OwnerCleared cleared -> {
                type = "owner";
                scope = cleared.scope().id();
                owner = cleared.owner();
            }
            case StorageEvent.ScopeCleared cleared -> {
                type = "scope";
                scope = cleared.scope().id();
            }
            case StorageEvent.SessionReleased released -> {
                type = "session";
                owner = released.playerId().toString();
            }
        }
        fields.put("type", type);
        fields.put("scope", scope);
        fields.put("owner", owner);
        fields.put("name", name);
        return fields;
    }

    /**
     * @return the event, or null when the fields do not describe one
     */
    public static @Nullable StorageEvent fromFields(@NotNull Map<String, String> fields) {
        String origin = fields.get("origin");
        String type = fields.get("type");
        VariableScope scope = VariableScope.parse(fields.get("scope"));
        String owner = fields.get("owner");
        String name = fields.get("name");
        if (origin == null || type == null) return null;
        return switch (type) {
            case "variable" -> scope == null || owner == null || name == null ? null : new StorageEvent.VariableChanged(origin, new VariableRef(scope, owner, name));
            case "name" -> scope == null || name == null ? null : new StorageEvent.NameCleared(origin, scope, name);
            case "owner" -> scope == null || owner == null ? null : new StorageEvent.OwnerCleared(origin, scope, owner);
            case "scope" -> scope == null ? null : new StorageEvent.ScopeCleared(origin, scope);
            case "session" -> owner == null ? null : new StorageEvent.SessionReleased(origin, UUID.fromString(owner));
            default -> null;
        };
    }
}
