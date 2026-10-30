package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.enums.dialog.DialogType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class DialogInventoryTypeRegistry {
    private static final DialogInventoryTypeRegistry instance;

    static {
        instance = new DialogInventoryTypeRegistry();
        instance.register(DialogType.NOTICE, new NoticeDialogInventoryTypeLoader());
        instance.register(DialogType.CONFIRMATION, new ConfirmationDialogInventoryTypeLoader());
        instance.register(DialogType.MULTI_ACTION, new MultiActionDialogInventoryTypeLoader());
        instance.register(DialogType.SERVER_LINKS, new ServerLinksDialogInventoryTypeLoader());
        instance.register(DialogType.DIALOG_LIST, new DialogListDialogInventoryTypeLoader());
    }

    private final Map<DialogType, DialogInventoryTypeLoader<?>> loaders = new EnumMap<>(DialogType.class);

    public static DialogInventoryTypeRegistry getInstance() {
        return instance;
    }

    public void register(DialogType type, DialogInventoryTypeLoader<?> loader) {
        this.loaders.put(type, loader);
    }

    public Optional<DialogInventoryTypeLoader<?>> get(DialogType type) {
        return Optional.ofNullable(this.loaders.get(type));
    }
}
