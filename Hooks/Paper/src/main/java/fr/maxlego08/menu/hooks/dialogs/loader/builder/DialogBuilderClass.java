package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DialogNumberRangeInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DilogBooleanInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DilogInputTextBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.input.DilogSingleOptionInputBuilder;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import fr.maxlego08.menu.zcore.logger.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class DialogBuilderClass {
    private static final Map<DialogBodyType, DialogBuilder> dialogBuilders = new HashMap<>();
    private static final Map<DialogInputType, DialogInputBuilderInt> dialogInputBuilders = new HashMap<>();
    private final MenuPlugin plugin;

    public DialogBuilderClass(MenuPlugin plugin) {
        this.plugin = plugin;
        this.loadBuilders();
    }

    private void loadBuilders() {
        this.registerBuilder(new PlainMessageDialogBuilder());
        this.registerBuilder(new ItemDialogBuilder());

        this.registerInputBuilder(new DilogInputTextBuilder());
        this.registerInputBuilder(new DilogBooleanInputBuilder());
        this.registerInputBuilder(new DilogSingleOptionInputBuilder());
        this.registerInputBuilder(new DialogNumberRangeInputBuilder());
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
