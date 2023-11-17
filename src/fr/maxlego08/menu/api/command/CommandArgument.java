package fr.maxlego08.menu.api.command;

import java.util.Optional;

/**
 * Represents an argument for a command.
 */
public interface CommandArgument {

    /**
     * Gets the command argument.
     *
     * @return The command argument.
     */
    String getArgument();

    /**
     * Gets the name of the inventory to open with the argument.
     *
     * @return The optional name of the inventory associated with the argument.
     */
    Optional<String> getInventory();

    /**
     * Checks if the argument is required.
     *
     * @return {@code true} if the argument is required, otherwise {@code false}.
     */
    boolean isRequired();
}
