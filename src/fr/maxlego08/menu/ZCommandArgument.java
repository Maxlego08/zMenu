package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.requirement.Action;

import java.util.List;
import java.util.Optional;

public class ZCommandArgument implements CommandArgument {

    private final String argument;
    private final String inventory;
    private final boolean isRequired;
    private final boolean performMainAction;
    private final List<Action> actions;
    private final List<String> autoCompletion;

    public ZCommandArgument(String argument, String inventory, boolean isRequired, boolean performMainAction, List<Action> actions, List<String> autoCompletion) {
        this.argument = argument;
        this.inventory = inventory;
        this.isRequired = isRequired;
        this.performMainAction = performMainAction;
        this.actions = actions;
        this.autoCompletion = autoCompletion;
    }

    @Override
    public boolean isRequired() {
        return isRequired;
    }

    @Override
    public List<Action> getActions() {
        return this.actions;
    }

    @Override
    public List<String> getAutoCompletion() {
        return this.autoCompletion;
    }

    @Override
    public boolean isPerformMainActions() {
        return this.performMainAction;
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
