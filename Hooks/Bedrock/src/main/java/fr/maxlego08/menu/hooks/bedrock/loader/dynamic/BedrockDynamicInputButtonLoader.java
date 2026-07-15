package fr.maxlego08.menu.hooks.bedrock.loader.dynamic;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.hooks.bedrock.button.buttons.BedrockDynamicInputButton;
import fr.maxlego08.menu.hooks.bedrock.loader.BedrockDynamicAbstractLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.BEDROCK_INVENTORY)
public class BedrockDynamicInputButtonLoader extends BedrockDynamicAbstractLoader {

    public BedrockDynamicInputButtonLoader(@NotNull MenuPlugin plugin) {
        super(plugin, "bedrock-dynamic-input-button");
    }

    @Override
    protected String getChildPath() {
        return "input.";
    }

    @Override
    protected @Nullable Button wrap(Button button, String buttonName, String start, String end) {
        if (button instanceof BedrockInputButton inputButton && !button.hasSpecialRender()) {
            inputButton.setKey(buttonName);
            return new BedrockDynamicInputButton(this.plugin, buttonName, start, end, inputButton);
        }
        return null;
    }
}
