package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.ZMenuKeys;
import fr.maxlego08.menu.api.utils.Message;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.key.Keyed;
import org.jetbrains.annotations.NotNull;

public interface CommandArgumentValidator extends Keyed {
    /**
     * Checks if the given value is valid for this validator.
     *
     * @param value The value to check.
     * @return {@code true} if the value is valid, otherwise {@code false}.
     */
    boolean isValid(String value);

    /**
     * Gets the error message to display when the given value is not valid for this validator.
     *
     * @return The error message.
     */
    Message getErrorMessage();

    /**
     * Gets the type of this argument validator. This is used to determine which
     * argument validators can be used with which argument types.
     *
     * @return The type of this argument validator.
     */
    String getType();

    /**
     * The key of this validator in {@link fr.maxlego08.menu.api.registry.Registries#COMMAND_ARGUMENT_VALIDATOR},
     * read from {@link #getType()} like a {@code type} written in a command file.
     *
     * @return the key
     */
    @Override
    default @NotNull Key key() {
        return ZMenuKeys.parse(this.getType());
    }
}
