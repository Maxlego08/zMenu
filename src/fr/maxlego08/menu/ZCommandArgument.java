package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.CommandArgument;

import java.util.Optional;

public class ZCommandArgument implements CommandArgument {

    private final String argument;
    private final String inventory;
    private final boolean isRequired;

    public ZCommandArgument(String argument, String inventory, boolean isRequired) {
        this.argument = argument;
        this.inventory = inventory;
        this.isRequired = isRequired;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public String getArgument() {
        return argument;
    }

    @Override
    public Optional<String> getInventory() {
        return Optional.ofNullable(inventory);
    }
}
