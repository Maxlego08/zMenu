package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.body.ItemBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.body.PlainMessageBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.BooleanInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.TextInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.NumberRangeInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.SingleOptionInputBuilder;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DialogBuilderClass {
    private static final Map<DialogBodyType, DialogBuilderBody> dialogBuilders = new HashMap<>();
    private static final Map<DialogInputType, DialogBuilderInput> dialogInputBuilders = new HashMap<>();
    private final MenuPlugin menuPlugin;
    public DialogBuilderClass(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.loadBuilders(dialogManager);
    }

    private void loadBuilders(ZDialogManager dialogManager) {
        this.registerBuilder(new PlainMessageBuilder(dialogManager, this.menuPlugin));
        this.registerBuilder(new ItemBuilder(dialogManager, this.menuPlugin));

        this.registerInputBuilder(new TextInputBuilder(dialogManager, this.menuPlugin));
        this.registerInputBuilder(new BooleanInputBuilder(dialogManager, this.menuPlugin));
        this.registerInputBuilder(new SingleOptionInputBuilder(dialogManager, this.menuPlugin));
        this.registerInputBuilder(new NumberRangeInputBuilder(dialogManager, this.menuPlugin));
    }

    public void registerBuilder(DialogBuilderBody builder) {
        if (dialogBuilders.containsKey(builder.getBodyType())) {
            Logger.info("DialogBuilder " + builder.getBodyType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogBuilders.put(builder.getBodyType(), builder);
        }
    }

    public void registerInputBuilder(DialogBuilderInput builder) {
        if (dialogInputBuilders.containsKey(builder.getBodyType())) {
            Logger.info("DialogInputBuilder " + builder.getBodyType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogInputBuilders.put(builder.getBodyType(), builder);
        }
    }

    public static Optional<DialogBuilderBody> getDialogBuilder(DialogBodyType type) {
        return Optional.ofNullable(dialogBuilders.get(type));
    }

    public static Optional<DialogBuilderInput> getDialogInputBuilder(DialogInputType type) {
        return Optional.ofNullable(dialogInputBuilders.get(type));
    }


}
