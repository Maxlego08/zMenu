package fr.maxlego08.menu.api.button.buttons.bedrock;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;

import java.util.List;

public abstract class BedrockDropDownInput extends InputButton {

    public BedrockDropDownInput(String text, List<SingleOption> singleOptionList) {
        super(DialogInputType.SINGLE_OPTION);
        this.setLabel(text);
        this.setSigleOptions(singleOptionList);
    }
}
