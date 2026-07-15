package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;

public abstract class BedrockBuilderManager {
    protected final MenuPlugin menuPlugin;

    public BedrockBuilderManager(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

}