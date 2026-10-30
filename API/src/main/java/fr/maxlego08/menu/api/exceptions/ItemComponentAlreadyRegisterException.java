package fr.maxlego08.menu.api.exceptions;

/**
 * @deprecated no longer thrown: an item component registered twice is refused by
 * {@link fr.maxlego08.menu.api.registry.Registries#ITEM_COMPONENT} with a
 * {@link fr.maxlego08.menu.api.registry.RegistrationException}.
 */
@Deprecated(since = "1.1.2.1", forRemoval = true)
public class ItemComponentAlreadyRegisterException extends RuntimeException {
    public ItemComponentAlreadyRegisterException(String message) {
        super(message);
    }
}
