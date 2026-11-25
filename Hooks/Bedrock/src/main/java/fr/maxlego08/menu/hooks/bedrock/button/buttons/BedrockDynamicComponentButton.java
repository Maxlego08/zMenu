package fr.maxlego08.menu.hooks.bedrock.button.buttons;

import fr.maxlego08.menu.api.button.buttons.bedrock.components.BasicBedrockComponentButton;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.hooks.bedrock.button.BedrockDynamicButton;
import fr.maxlego08.menu.hooks.bedrock.button.ExpandedBedrockComponentButton;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockDynamicAbstractLoader;
import fr.robie.minimessageflow.TextTemplate;
import org.geysermc.cumulus.component.ButtonComponent;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BedrockDynamicComponentButton extends BedrockComponentButton implements BedrockDynamicButton<BedrockComponentButton, ButtonComponent> {
    private final TextTemplate start;
    private final TextTemplate end;
    private final BedrockComponentButton button;

    public BedrockDynamicComponentButton(String start, String end, BasicBedrockComponentButton button) {
        this.start = TextService.get().flow().compileText(start);
        this.end = TextService.get().flow().compileText(end);
        this.button = button;
    }

    @Override
    public void onRender(@NotNull BedrockRenderContext<ButtonComponent> context) {
        this.onRender(context, button1 -> {
        });
    }

    @Override
    public void onRender(BedrockRenderContext<ButtonComponent> context, Consumer<BedrockComponentButton> expandedButtonConsumer) {
        int startValue = BedrockDynamicAbstractLoader.stringToInt(this.start, context.getPlaceholders(), context.getPlayer());
        int endValue = BedrockDynamicAbstractLoader.stringToInt(this.end, context.getPlaceholders(), context.getPlayer());

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
