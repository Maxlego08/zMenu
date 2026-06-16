package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.bedrock.ButtonBuilder;

public class BedrockBuilderClass {
    private final ButtonBuilder bedrockButtonBuilder;
    private final MenuPlugin menuPlugin;

    public BedrockBuilderClass(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.bedrockButtonBuilder = new ButtonBuilder(this.menuPlugin);
    }


    public ButtonBuilder getBedrockButtonBuilder() {
        return this.bedrockButtonBuilder;
    }


}
