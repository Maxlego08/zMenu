package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.bedrock.ButtonBuilder;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.input.*;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class BedrockBuilderClass {
    private static final Map<DialogInputType, BedrockBuilderInput> dialogInputBuilders = new HashMap<>();
    private final ButtonBuilder bedrockButtonBuilder;
    private final MenuPlugin menuPlugin;

    public BedrockBuilderClass(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.loadBuilders();
        this.bedrockButtonBuilder = new ButtonBuilder(this.menuPlugin);
    }

    private void loadBuilders() {
        this.registerInputBuilder(new DropdownInputBuilder(this.menuPlugin));
        this.registerInputBuilder(new InputTextBuilder(this.menuPlugin));
        this.registerInputBuilder(new LabelBuilder(this.menuPlugin));
        this.registerInputBuilder(new SliderInputBuilder(this.menuPlugin));
        this.registerInputBuilder(new ToggleInputBuilder(this.menuPlugin));
    }

    public void registerInputBuilder(BedrockBuilderInput builder) {
        if (dialogInputBuilders.containsKey(builder.getType())) {
            Logger.info("DialogInputBuilder " + builder.getType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogInputBuilders.put(builder.getType(), builder);
        }
    }

    public ButtonBuilder getBedrockButtonBuilder() {
        return this.bedrockButtonBuilder;
    }

    public static Optional<BedrockBuilderInput> getDialogInputBuilder(DialogInputType type) {
        return Optional.ofNullable(dialogInputBuilders.get(type));
    }


}
