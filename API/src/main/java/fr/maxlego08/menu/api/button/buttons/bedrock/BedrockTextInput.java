package fr.maxlego08.menu.api.button.buttons.bedrock;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class BedrockTextInput extends InputButton {

    public BedrockTextInput(String text, String defaultValue) {
        super(DialogInputType.TEXT);
        this.setLabel(text);
        this.setDefaultText(defaultValue);
    }
}
