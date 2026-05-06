package fr.maxlego08.menu.hooks.dialogs.button.buttons;

import fr.maxlego08.menu.api.button.buttons.dialogs.DialogItemBody;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;

import java.util.List;

public class ZDialogItemBody extends DialogItemBody {

    public ZDialogItemBody(
            List<String> descriptionMessages,
            int descriptionWidth,
            boolean showDecoration,
            boolean showTooltip,
            boolean useCache,
            int width,
            int height
    ) {
        super(descriptionMessages, descriptionWidth, showDecoration, showTooltip, useCache, width, height);
    }
}
