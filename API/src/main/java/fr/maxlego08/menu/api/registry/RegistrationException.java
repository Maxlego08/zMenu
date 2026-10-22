package fr.maxlego08.menu.api.registry;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown when a value cannot be registered: the registry is frozen, the key is already taken, or
 * the value is not of the type the registry holds.
 */
public final class RegistrationException extends RuntimeException {

    public RegistrationException(@NotNull String message) {
        super(message);
    }
}
