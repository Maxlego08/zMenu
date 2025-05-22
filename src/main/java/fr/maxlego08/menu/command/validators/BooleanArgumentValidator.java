package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;

public class BooleanArgumentValidator implements CommandArgumentValidator {
    @Override
    public boolean isValid(String value) {
        return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false");
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_BOOLEAN;
    }
}