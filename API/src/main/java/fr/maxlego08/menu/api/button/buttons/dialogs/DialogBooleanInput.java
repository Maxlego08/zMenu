package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class DialogBooleanInput extends InputButton {

    public DialogBooleanInput(String text, String defaultValue, String textTrue, String textFalse) {
        super(DialogInputType.BOOLEAN);
        this.setLabel(text);
        this.setInitialValueBool(defaultValue);
        this.setTextTrue(textTrue);
        this.setTextFalse(textFalse);
    }

    public DialogBooleanInput(String text, String defaultValue) {
        super(DialogInputType.BOOLEAN);
        this.setLabel(text);
        this.setInitialValueBool(defaultValue);
        this.setTextTrue("True");
        this.setTextFalse("False");
    }

    public DialogBooleanInput(String text) {
        super(DialogInputType.BOOLEAN);
        this.setLabel(text);
        this.setInitialValueBool(String.valueOf(true));
        this.setTextTrue("True");
        this.setTextFalse("False");
    }
}
