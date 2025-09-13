package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;

import java.util.List;

public abstract class DialogSingleOptionInput extends InputButton {

    public DialogSingleOptionInput(String text, boolean textVisible, List<SingleOption> singleOptionList) {
        super(DialogInputType.SINGLE_OPTION);
        this.setLabel(text);
        this.setLabelVisible(textVisible);
        this.setSigleOptions(singleOptionList);
    }

    public DialogSingleOptionInput(String text, List<SingleOption> singleOptionList) {
        super(DialogInputType.SINGLE_OPTION);
        this.setLabel(text);
        this.setLabelVisible(true);
        this.setSigleOptions(singleOptionList);
    }

    public DialogSingleOptionInput(List<SingleOption> singleOptionList) {
        super(DialogInputType.SINGLE_OPTION);
        this.setLabelVisible(false);
        this.setSigleOptions(singleOptionList);
    }
}
