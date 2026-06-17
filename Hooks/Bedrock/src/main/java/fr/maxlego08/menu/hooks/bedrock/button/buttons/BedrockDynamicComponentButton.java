package fr.maxlego08.menu.hooks.bedrock.button.buttons;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BasicBedrockComponentButton;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.hooks.bedrock.button.BedrockDynamicButton;
import fr.maxlego08.menu.hooks.bedrock.button.ExpandedBedrockComponentButton;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockDynamicAbstractLoader;
import org.geysermc.cumulus.component.ButtonComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BedrockDynamicComponentButton extends BedrockComponentButton implements BedrockDynamicButton<BedrockComponentButton, ButtonComponent> {
    private final MenuPlugin plugin;
    private final String start;
    private final String end;
    private final BedrockComponentButton button;

    public BedrockDynamicComponentButton(MenuPlugin plugin, String start, String end, BasicBedrockComponentButton button) {
        this.plugin = plugin;
        this.start = start;
        this.end = end;
        this.button = button;
    }

    @Override
    public void onRender(@NotNull BedrockRenderContext<ButtonComponent> context) {
        this.onRender(context, button1 -> {
        });
    }

    @Override
    public void onRender(BedrockRenderContext<ButtonComponent> context, Consumer<BedrockComponentButton> expandedButtonConsumer) {
        int startValue = BedrockDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = BedrockDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());

        for (int i = startValue; i <= endValue; i++) {
            context.getPlaceholders().register("index", String.valueOf(i));
            ButtonComponent build = this.button.build(context);
            if (build != null) {
                context.getContent().add(build);
                expandedButtonConsumer.accept(new ExpandedBedrockComponentButton(this.button, i));
            }
        }
    }

    @Override
    public boolean hasSpecialRender() {
        return true;
    }
}
