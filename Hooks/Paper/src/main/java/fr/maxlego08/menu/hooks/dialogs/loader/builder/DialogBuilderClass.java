package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.body.ItemBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.body.PlainMessageBuilder;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DialogBuilderClass {
    private static final Map<DialogBodyType, DialogBuilderBody> dialogBuilders = new HashMap<>();
    private final MenuPlugin menuPlugin;
    public DialogBuilderClass(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.loadBuilders(dialogManager);
    }

    private void loadBuilders(ZDialogManager dialogManager) {
        this.registerBuilder(new PlainMessageBuilder(dialogManager, this.menuPlugin));
        this.registerBuilder(new ItemBuilder(dialogManager, this.menuPlugin));
    }

    public void registerBuilder(DialogBuilderBody builder) {
        if (dialogBuilders.containsKey(builder.getBodyType())) {
            Logger.info("DialogBuilder " + builder.getBodyType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogBuilders.put(builder.getBodyType(), builder);
        }
    }

    public static Optional<DialogBuilderBody> getDialogBuilder(DialogBodyType type) {
        return Optional.ofNullable(dialogBuilders.get(type));
    }


}
