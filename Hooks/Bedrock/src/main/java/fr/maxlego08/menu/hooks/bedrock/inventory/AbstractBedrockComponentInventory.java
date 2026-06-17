package fr.maxlego08.menu.hooks.bedrock.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.hooks.bedrock.AbstractBedrockInventory;
import org.geysermc.cumulus.form.Form;
import org.geysermc.cumulus.form.util.FormBuilder;
import org.geysermc.cumulus.response.FormResponse;

import java.util.List;

public abstract class AbstractBedrockComponentInventory<B extends FormBuilder<B, F, R>, F extends Form, R extends FormResponse, Bu extends BedrockComponentButton> extends AbstractBedrockInventory<B, F, R> {
    protected final List<Bu> bodyButtons;
    protected final String content;

    public AbstractBedrockComponentInventory(MenuPlugin plugin, String fileName, String name, String content, List<Bu> bodyButtons, BedrockType bedrockType) {
        super(plugin, fileName, name, bedrockType);
        this.bodyButtons = bodyButtons;
        this.content = content;
    }
}
