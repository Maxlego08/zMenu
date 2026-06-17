package fr.maxlego08.menu.hooks.bedrock.loader.dynamic;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BasicBedrockComponentButton;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.BedrockDynamicComponentButton;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockDynamicAbstractLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockDynamicComponentButtonLoader extends BedrockDynamicAbstractLoader {

    public BedrockDynamicComponentButtonLoader(@NotNull MenuPlugin plugin) {
        super(plugin, "bedrock-dynamic-button");
    }

    @Override
    protected String getChildPath() {
        return "button.";
    }

    @Override
    protected @Nullable Button wrap(Button button, String buttonName, String start, String end) {

        if (button instanceof BasicBedrockComponentButton componentButton && !button.hasSpecialRender()) {
            return new BedrockDynamicComponentButton(this.plugin, start, end, componentButton);
        }
        return null;
    }
}
