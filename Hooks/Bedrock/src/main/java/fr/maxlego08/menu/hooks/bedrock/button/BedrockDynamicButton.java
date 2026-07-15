package fr.maxlego08.menu.hooks.bedrock.button;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.BedrockRenderContext;

import java.util.function.Consumer;

public interface BedrockDynamicButton<T extends Button, C> {

    void onRender(BedrockRenderContext<C> context, Consumer<T> expandedButtonConsumer);

}
