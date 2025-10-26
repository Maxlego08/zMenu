package fr.maxlego08.menu.api.button.buttons.bedrock;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class BedrockSliderInput extends InputButton {

    public BedrockSliderInput(String text, float start, float end, float step, String initialValue) {
        super(DialogInputType.NUMBER_RANGE);
        this.setLabel(text);
        this.setStart(start);
        this.setEnd(end);
        this.setStep(step);
        this.setInitialValueRange(initialValue);
    }

    public BedrockSliderInput(String text, float start, float end, float step) {
        super(DialogInputType.NUMBER_RANGE);
        this.setLabel(text);
        this.setStart(start);
        this.setEnd(end);
        this.setStep(step);
        this.setInitialValueRange(String.valueOf((end + start) / 2));
    }
}