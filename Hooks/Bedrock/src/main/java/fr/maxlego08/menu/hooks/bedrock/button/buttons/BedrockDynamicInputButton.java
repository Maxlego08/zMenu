package fr.maxlego08.menu.hooks.bedrock.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.button.BedrockDynamicButton;
import fr.maxlego08.menu.hooks.bedrock.button.ExpandedBedrockInputButton;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockDynamicAbstractLoader;
import org.geysermc.cumulus.component.Component;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BedrockDynamicInputButton extends BedrockInputButton implements BedrockDynamicButton<BedrockInputButton, Component> {
    private final MenuPlugin plugin;
    private final String buttonName;
    private final String start;
    private final String end;
    private final BedrockInputButton button;

    public BedrockDynamicInputButton(MenuPlugin plugin, String buttonName, String start, String end, BedrockInputButton button) {
        this.plugin = plugin;
        this.buttonName = buttonName;
        this.start = start;
        this.end = end;
        this.button = button;
    }

    @Override
    public void onRender(@NotNull BedrockRenderContext<Component> context) {
        this.onRender(context, button1 -> {
        });
    }

    @Override
    public void onRender(BedrockRenderContext<Component> context, Consumer<BedrockInputButton> expandedButtonConsumer) {
        Placeholders placeholders = context.getPlaceholders();
        int startValue = BedrockDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = BedrockDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());

        for (int i = startValue; i <= endValue; i++) {
            placeholders.register("index", String.valueOf(i));
            String key = this.buttonName + "_" + i;
            Component build = this.button.build(context);
            if (build != null) {
                context.getContent().add(build);
                expandedButtonConsumer.accept(new ExpandedBedrockInputButton(this.button, key));
            }
        }
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }
}
