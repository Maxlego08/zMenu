package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.enums.dialog.DialogType;
import fr.maxlego08.menu.api.registry.Registry;

public class DialogInventoryTypeRegistry extends Registry<DialogType, DialogInventoryTypeLoader<?>> {
    private static final DialogInventoryTypeRegistry instance;

    static {
        instance = new DialogInventoryTypeRegistry();
        instance.register(DialogType.NOTICE, new NoticeDialogInventoryTypeLoader());
        instance.register(DialogType.CONFIRMATION, new ConfirmationDialogInventoryTypeLoader());
        instance.register(DialogType.MULTI_ACTION, new MultiActionDialogInventoryTypeLoader());
        instance.register(DialogType.SERVER_LINKS, new ServerLinksDialogInventoryTypeLoader());
    }

    public static DialogInventoryTypeRegistry getInstance() {
        return instance;
    }

}
