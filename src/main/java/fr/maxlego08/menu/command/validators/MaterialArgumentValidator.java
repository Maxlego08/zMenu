package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.Material;

public class MaterialArgumentValidator implements CommandArgumentValidator {

    private final Message message;

    public MaterialArgumentValidator(Message message) {
        this.message = message;
    }

    @Override
    public boolean isValid(String value) {
        try {
            return Material.matchMaterial(value.toUpperCase()) != null;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public Message getErrorMessage() {
        return this.message;
    }

    @Override
    public String getType() {
        return "material";
    }
}
