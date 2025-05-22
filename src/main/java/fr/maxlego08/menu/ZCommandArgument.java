package fr.maxlego08.menu;

import fr.maxlego08.menu.api.command.CommandArgument;
import fr.maxlego08.menu.api.command.CommandArgumentType;
import fr.maxlego08.menu.api.requirement.Action;

import java.util.List;
import java.util.Optional;

public class ZCommandArgument implements CommandArgument {

    private final CommandArgumentType commandArgumentType;
    private final String argument;
    private final String inventory;
    private final boolean isRequired;
    private final boolean performMainAction;
    private final List<Action> actions;
    private final List<String> autoCompletion;
    private final String defaultValue;

    public ZCommandArgument(CommandArgumentType commandArgumentType, String argument, String inventory, boolean isRequired, boolean performMainAction, List<Action> actions, List<String> autoCompletion, String defaultValue) {
        this.commandArgumentType = commandArgumentType;
        this.argument = argument;
        this.inventory = inventory;
        this.isRequired = isRequired;
        this.performMainAction = performMainAction;
        this.actions = actions;
        this.autoCompletion = autoCompletion;
        this.defaultValue = defaultValue;
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
    public String getDefaultValue() {
        return this.defaultValue;
    }

    @Override
    public CommandArgumentType getType() {
        return this.commandArgumentType;
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
