package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;
import org.bukkit.Material;

import java.util.Locale;

public class MaterialArgumentValidator implements CommandArgumentValidator {

    private final String type;
    private final Message message;
    private final boolean blocksOnly;

    private MaterialArgumentValidator(String type, Message message, boolean blocksOnly) {
        this.type = type;
        this.message = message;
        this.blocksOnly = blocksOnly;
    }

    /**
     * @return the {@code material} type, any material
     */
    public static MaterialArgumentValidator material() {
        return new MaterialArgumentValidator("material", Message.COMMAND_ARGUMENT_MATERIAL, false);
    }

    /**
     * @return the {@code block} type, a material that can be placed
     */
    public static MaterialArgumentValidator block() {
        return new MaterialArgumentValidator("block", Message.COMMAND_ARGUMENT_BLOCK, true);
    }

    @Override
    public boolean isValid(String value) {
        try {
            Material material = Material.matchMaterial(value.toUpperCase(Locale.ROOT));
            return material != null && (!this.blocksOnly || material.isBlock());
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
        return this.type;
    }
}
