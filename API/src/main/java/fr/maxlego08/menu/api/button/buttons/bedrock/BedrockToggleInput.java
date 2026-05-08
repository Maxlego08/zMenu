package fr.maxlego08.menu.api.button.buttons.bedrock;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class BedrockToggleInput extends InputButton {

    public BedrockToggleInput(String text, String defaultValue) {
        super(DialogInputType.BOOLEAN);
        this.setLabel(text);
        this.setInitialValueBool(defaultValue);
    }
    public BedrockToggleInput(String text) {
        super(DialogInputType.BOOLEAN);
        this.setLabel(text);
        this.setInitialValueBool(String.valueOf(true));
    }
}