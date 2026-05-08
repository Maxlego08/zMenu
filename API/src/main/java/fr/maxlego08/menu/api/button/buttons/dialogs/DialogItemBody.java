package fr.maxlego08.menu.api.button.buttons.dialogs;

import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import java.util.List;

public class DialogItemBody extends BodyButton {

    public DialogItemBody(
            List<String> descriptionMessages,
            int descriptionWidth,
            boolean showDecoration,
            boolean showTooltip,
            boolean useCache,
            int width,
            int height
    ) {
        super(DialogBodyType.ITEM);
        this.setUseCache(useCache);
        this.setShowDecorations(showDecoration);
        this.setShowTooltip(showTooltip);
        this.setDescriptionMessages(descriptionMessages);
        this.setDescriptionWidth(descriptionWidth);
        this.setWidth(width);
        this.setHeight(height);
    }
}
