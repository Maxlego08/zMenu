package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.record.dialogs.ActionButtonRecord;
import fr.maxlego08.menu.config.ConfigFieldContext;
import fr.maxlego08.menu.hooks.dialogs.inventory.ZConfirmationDialogInventory;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class ZDialogInventoryDeveloper extends ZConfirmationDialogInventory {

    private final ConfigFieldContext context;

    public ZDialogInventoryDeveloper(MenuPlugin plugin, String name, String fileName, String externalTitle, ConfigFieldContext context, ActionButtonRecord yesActionButtonRecord, ActionButtonRecord noActionButtonRecord) {
        super(plugin, name, fileName, externalTitle, yesActionButtonRecord, noActionButtonRecord);
        this.context = context;
    }

    public ConfigFieldContext getContext() {
        return this.context;
    }

    @Override
    protected @NotNull List<DialogInput> getDialogInputsForPlayer(@NotNull Player player, @NotNull PaperMetaUpdater componentMeta) {
        List<DialogInput> inputs = new ArrayList<>(this.context.size());
        for (ConfigFieldContext.ConfigFieldEntry entry : this.context.getEntries().values()) {
            Field field = entry.field();
            var configOption = entry.configOption();
            String key = configOption.key().isEmpty() ? field.getName() : configOption.key();
            DialogInput input = switch (entry.resolvedType()) {
                case BOOLEAN -> this.buildBooleanInput(key, field, configOption, componentMeta);
                case NUMBER_RANGE -> this.buildNumberRangeInput(key, field, configOption, componentMeta);
                case TEXT -> this.buildTextInput(key, field, configOption, componentMeta);
                case SINGLE_OPTION -> this.buildSingleOptionInput(key, field, configOption, componentMeta);
            };
            if (input != null) {
                inputs.add(input);
            }
        }
        return inputs;
    }

    private DialogInput buildBooleanInput(String key, Field field, fr.maxlego08.menu.api.configuration.annotation.ConfigOption configOption, PaperMetaUpdater componentMeta) {
        boolean initialValue;
        try {
            initialValue = field.getBoolean(null);
        } catch (IllegalAccessException e) {
            initialValue = false;
        }
        return DialogInput.bool(
                key,
                componentMeta.getComponent(configOption.label()),
                initialValue,
                configOption.trueText(),
                configOption.falseText()
        );
    }

    private DialogInput buildNumberRangeInput(String key, Field field, fr.maxlego08.menu.api.configuration.annotation.ConfigOption configOption, PaperMetaUpdater componentMeta) {
        float initialValue;
        try {
            initialValue = field.getFloat(null);
        } catch (IllegalAccessException e) {
            initialValue = (configOption.startRange() + configOption.endRange()) / 2;
        }
        return DialogInput.numberRange(
                key,
                configOption.width(),
                componentMeta.getComponent(configOption.label()),
                configOption.labelFormat(),
                configOption.startRange(),
                configOption.endRange(),
                initialValue,
                configOption.stepRange()
        );
    }

    private DialogInput buildTextInput(String key, Field field, fr.maxlego08.menu.api.configuration.annotation.ConfigOption configOption, PaperMetaUpdater componentMeta) {
        String defaultText;
        try {
            defaultText = (String) field.get(null);
        } catch (IllegalAccessException e) {
            defaultText = "";
        }
        if (defaultText == null) {
            defaultText = "";
        }
        return DialogInput.text(
                key,
                configOption.width(),
                componentMeta.getComponent(configOption.label()),
                configOption.labelVisible(),
                defaultText,
                configOption.maxLength(),
                null
        );
    }

    private DialogInput buildSingleOptionInput(String key, Field field, fr.maxlego08.menu.api.configuration.annotation.ConfigOption configOption, PaperMetaUpdater componentMeta) {
        Class<?> fieldType = field.getType();
        if (!fieldType.isEnum()) {
            return null;
        }
        Object[] constants = fieldType.getEnumConstants();
        if (constants == null || constants.length == 0) {
            return null;
        }
        String currentValue;
        try {
            Enum<?> current = (Enum<?>) field.get(null);
            currentValue = current != null ? current.name() : "";
        } catch (IllegalAccessException e) {
            currentValue = "";
        }
        List<SingleOptionDialogInput.OptionEntry> options = new ArrayList<>(constants.length);
        for (Object constant : constants) {
            Enum<?> enumConstant = (Enum<?>) constant;
            options.add(SingleOptionDialogInput.OptionEntry.create(
                    enumConstant.name(),
                    componentMeta.getComponent(enumConstant.name()),
                    enumConstant.name().equals(currentValue)
            ));
        }
        return DialogInput.singleOption(
                key,
                configOption.width(),
                options,
                componentMeta.getComponent(configOption.label()),
                configOption.labelVisible()
        );
    }

}
