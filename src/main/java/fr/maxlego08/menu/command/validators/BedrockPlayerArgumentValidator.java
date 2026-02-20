package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;

public class BedrockPlayerArgumentValidator implements CommandArgumentValidator {

    private final BedrockManager bedrockManager;
    private final Boolean geyserSupport;

    public BedrockPlayerArgumentValidator(BedrockManager bedrockManager) {
        this.bedrockManager = bedrockManager;
        this.geyserSupport = bedrockManager != null;
    }

    @Override
    public boolean isValid(String value) {
        return this.geyserSupport && bedrockManager.isBedrockPlayer(value);
    }

    @Override
    public Message getErrorMessage() {
        return Message.COMMAND_ARGUMENT_ONLINE_PLAYER;
    }

    @Override
    public String getType() {
        return "bedrock-player";
    }
}
