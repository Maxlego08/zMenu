package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.entity.EntityType;

public class EntityTypeArgumentValidator implements CommandArgumentValidator {

    @Override
    public boolean isValid(String value) {
        try {
            return EntityType.valueOf(value.toUpperCase()) != null;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_ENTITY;
    }

    @Override
    public String getType() {
        return "entity-type";
    }
}
