package fr.maxlego08.menu.api.command;

import java.util.Optional;

public interface CommandArgument {

    /**
     * Get command argument
     *
     * @return argument
     */
    String getArgument();

    /**
     * Get the inventory that will be opened with the argument
     *
     * @return inventory
     */
    Optional<String> getInventory();

    /**
     * Check if argument is required
     *
     * @return boolean
     */
    boolean isRequired();

}
