package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.ZMenuPlugin;
import fr.maxlego08.menu.api.command.CommandArgumentType;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;

import java.util.Optional;

public class CommandArgumentValidatorFactory {

    public static Optional<CommandArgumentValidator> getValidator(ZMenuPlugin plugin, CommandArgumentType type) {
        return switch (type) {
            case INT -> Optional.of(new IntArgumentValidator());
            case DOUBLE -> Optional.of(new DoubleArgumentValidator());
            case BOOLEAN -> Optional.of(new BooleanArgumentValidator());
            case MATERIAL -> Optional.of(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_MATERIAL));
            case BLOCK -> Optional.of(new MaterialArgumentValidator(Message.COMMAND_ARGUMENT_BLOCK));
            case ENTITY_TYPE -> Optional.of(new EntityTypeArgumentValidator());
            case ONLINE_PLAYER -> Optional.of(new OnlinePlayerArgumentValidator(plugin));
            case PLAYER -> Optional.of(new PlayerArgumentValidator(plugin));
            case LOCATION -> Optional.of(new LocationArgumentValidator(plugin));
            case WORLD -> Optional.of(new WorldArgumentValidator(plugin));
            default -> Optional.empty(); // Pas de validation spécifique pour STRING par exemple
        };
    }
}
