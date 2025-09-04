package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.enums.DialogBodyType;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DialogBooleanInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DialogInputTextBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DialogNumberRangeInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DialogSingleOptionInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DialogBuilderClass {
    private static final Map<DialogBodyType, DialogBuilder> dialogBuilders = new HashMap<>();
    private static final Map<DialogInputType, DialogInputBuilderInt> dialogInputBuilders = new HashMap<>();

    public DialogBuilderClass(ZDialogManager dialogManager) {
        this.loadBuilders(dialogManager);
    }

    private void loadBuilders(ZDialogManager dialogManager) {
        this.registerBuilder(new PlainMessageDialogBuilder(dialogManager));
        this.registerBuilder(new ItemDialogBuilder(dialogManager));

        this.registerInputBuilder(new DialogInputTextBuilder(dialogManager));
        this.registerInputBuilder(new DialogBooleanInputBuilder(dialogManager));
        this.registerInputBuilder(new DialogSingleOptionInputBuilder(dialogManager));
        this.registerInputBuilder(new DialogNumberRangeInputBuilder(dialogManager));
    }

    public void registerBuilder(DialogBuilder builder) {
        if (dialogBuilders.containsKey(builder.getBodyType())) {
            Logger.info("DialogBuilder " + builder.getBodyType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogBuilders.put(builder.getBodyType(), builder);
        }
    }

    public void registerInputBuilder(DialogInputBuilderInt builder) {
        if (dialogInputBuilders.containsKey(builder.getBodyType())) {
            Logger.info("DialogInputBuilder " + builder.getBodyType() + " is already registered!", Logger.LogType.WARNING);
        } else {
            dialogInputBuilders.put(builder.getBodyType(), builder);
        }
    }

    public static Optional<DialogBuilder> getDialogBuilder(DialogBodyType type) {
        return Optional.ofNullable(dialogBuilders.get(type));
    }

    public static Optional<DialogInputBuilderInt> getDialogInputBuilder(DialogInputType type) {
        return Optional.ofNullable(dialogInputBuilders.get(type));
    }


}
