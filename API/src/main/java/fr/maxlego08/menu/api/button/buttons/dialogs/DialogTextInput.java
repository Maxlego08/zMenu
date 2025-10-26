package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

public abstract class DialogTextInput extends InputButton {

    public DialogTextInput(
            String text,
            Boolean textVisible,
            String defaultValue,
            int width,
            int maxLength,
            int multilineMaxLines,
            int multilineHeight
    ) {
        super(DialogInputType.TEXT);
        this.setLabel(text);
        this.setLabelVisible(textVisible);
        this.setDefaultText(defaultValue);
        this.setWidth(width);
        this.setMaxLength(maxLength);
        if (multilineMaxLines > 0) {
            this.setMultilineMaxLines(multilineMaxLines);
        }
        if (multilineHeight > 0) {
            this.setMultilineHeight(multilineHeight);
        }
    }

    public DialogTextInput(
            String text,
            Boolean textVisible,
            String defaultValue,
            int maxLength
    ) {
        super(DialogInputType.TEXT);
        this.setLabel(text);
        this.setLabelVisible(textVisible);
        this.setDefaultText(defaultValue);
        this.setMaxLength(maxLength);
        this.setWidth(200);
    }
}
