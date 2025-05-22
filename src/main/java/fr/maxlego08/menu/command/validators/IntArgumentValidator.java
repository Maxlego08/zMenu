package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;

public class IntArgumentValidator implements CommandArgumentValidator {
    @Override
    public boolean isValid(String value) {
        try {
            Integer.parseInt(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_INTEGER;
    }
}
