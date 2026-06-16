package fr.maxlego08.menu.command.validators;

import fr.maxlego08.menu.api.BedrockManager;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoCommandArgumentValidator;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.command.CommandArgumentValidator;
import fr.maxlego08.menu.api.utils.Message;

@AutoCommandArgumentValidator
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockPlayerArgumentValidator implements CommandArgumentValidator {

    private final BedrockManager bedrockManager;
    private final Boolean geyserSupport;

    public BedrockPlayerArgumentValidator(MenuPlugin plugin) {
        this.bedrockManager = plugin.getBedrockManager();
        this.geyserSupport = this.bedrockManager != null;
    }

    @Override
    public boolean isValid(String value) {
        return this.geyserSupport && this.bedrockManager.isBedrockPlayer(value);
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
