package fr.maxlego08.menu.api.button.buttons.dialogs.body;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;

public abstract class VanillaDialogBody extends BodyButton {
    protected final DialogBodyType bodyType;
    protected final int width;

    public VanillaDialogBody(DialogBodyType bodyType, int width) {
        this.bodyType = bodyType;
        this.width = width;
    }
}
