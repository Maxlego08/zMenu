package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;

import java.util.List;

public class DialogPlainMessageBody extends BodyButton {

    public DialogPlainMessageBody(List<String> messages, int width) {
        super(DialogBodyType.PLAIN_MESSAGE);
        this.setMessages(messages);
        this.setWidth(width);
    }

    public DialogPlainMessageBody(List<String> messages) {
        super(DialogBodyType.PLAIN_MESSAGE);
        this.setMessages(messages);
        this.setWidth(128);
    }
}
