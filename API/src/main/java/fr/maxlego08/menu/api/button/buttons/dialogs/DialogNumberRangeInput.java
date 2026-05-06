package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class DialogNumberRangeInput extends InputButton {

    public DialogNumberRangeInput(
            String text,
            float start,
            float end,
            float step,
            String initialValue,
            int width,
            String labelFormat
    ) {
        super(DialogInputType.NUMBER_RANGE);
        this.setLabel(text);
        this.setStart(start);
        this.setEnd(end);
        this.setStep(step);
        this.setInitialValueRange(initialValue);
        this.setWidth(width);
        this.setLabelFormat(labelFormat);
    }

    public DialogNumberRangeInput(
            String text,
            float start,
            float end,
            float step
    ) {
        super(DialogInputType.NUMBER_RANGE);
        this.setLabel(text);
        this.setStart(start);
        this.setEnd(end);
        this.setStep(step);
        this.setInitialValueRange(String.valueOf((end + start) / 2));
        this.setWidth(200);
        this.setLabelFormat("options.generic_value");
    }
}