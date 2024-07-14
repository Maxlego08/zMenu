package fr.maxlego08.menu.api.command;

import fr.maxlego08.menu.api.requirement.Action;

import java.util.List;
import java.util.Optional;

/**
 * Represents an argument for a command.
 */
public interface CommandArgument {

    /**
     * Gets the command argument.
     *
     * @return The command argument as a String.
     */
    String getArgument();

    /**
     * Gets the name of the inventory to open with the argument.
     *
     * @return An Optional containing the name of the inventory associated with the argument if present, otherwise an empty Optional.
     */
    Optional<String> getInventory();

    /**
     * Checks if the argument is required.
     *
     * @return {@code true} if the argument is required, otherwise {@code false}.
     */
    boolean isRequired();

    /**
     * Gets the list of actions associated with this command argument.
     *
     * @return A list of Action objects associated with the argument.
     */
    List<Action> getActions();

    /**
     * Gets a list of strings that can be used for auto-completion of this argument.
     *
     * @return A list of strings that can be used to auto-complete the argument.
     */
    List<String> getAutoCompletion();

    /**
     * Checks if the main actions should be performed when this argument is used.
     *
     * @return {@code true} if the main actions should be performed, otherwise {@code false}.
     */
    boolean isPerformMainActions();
}
