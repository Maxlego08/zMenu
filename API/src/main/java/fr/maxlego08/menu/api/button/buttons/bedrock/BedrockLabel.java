package fr.maxlego08.menu.api.button.buttons.bedrock;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class BedrockLabel extends InputButton {

    public BedrockLabel(String text) {
        super(DialogInputType.BEDROCK_LABEL);
        this.setLabel(text);
    }
}