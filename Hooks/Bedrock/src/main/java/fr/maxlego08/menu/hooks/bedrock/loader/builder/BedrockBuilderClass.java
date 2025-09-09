package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.input.*;
import fr.maxlego08.menu.hooks.bedrock.utils.loader.BedrockInputBuilder;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BedrockBuilderClass {
    private static final Map<DialogInputType, BedrockInputBuilder> dialogInputBuilders = new HashMap<>();
    private final BedrockButtonBuilder bedrockButtonBuilder;
    private final MenuPlugin menuPlugin;

    public BedrockBuilderClass(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.loadBuilders();
        this.bedrockButtonBuilder = new BedrockButtonBuilder(this.menuPlugin);
    }

    private void loadBuilders() {
        this.registerInputBuilder(new BedrockDropdownInputBuilder(this.menuPlugin));
        this.registerInputBuilder(new BedrockInputTextBuilder(this.menuPlugin));
        this.registerInputBuilder(new BedrockLabelBuilder(this.menuPlugin));
        this.registerInputBuilder(new BedrockSliderInputBuilder(this.menuPlugin));
        this.registerInputBuilder(new BedrockToggleInputBuilder(this.menuPlugin));
    }

    public void registerInputBuilder(BedrockInputBuilder builder) {
        if (dialogInputBuilders.containsKey(builder.getType())) {
            Logger.info("DialogInputBuilder " + builder.getType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogInputBuilders.put(builder.getType(), builder);
        }
    }

    public BedrockButtonBuilder getBedrockButtonBuilder() {
        return this.bedrockButtonBuilder;
    }

    public static Optional<BedrockInputBuilder> getDialogInputBuilder(DialogInputType type) {
        return Optional.ofNullable(dialogInputBuilders.get(type));
    }


}
