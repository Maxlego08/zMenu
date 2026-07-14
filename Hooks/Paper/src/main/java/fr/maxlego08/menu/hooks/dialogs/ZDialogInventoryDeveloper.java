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
                case BOOLEAN -> this.buildBooleanInput(key, entry, componentMeta);
                case NUMBER_RANGE -> this.buildNumberRangeInput(key, entry, componentMeta);
                case TEXT -> this.buildTextInput(key, entry, componentMeta);
                case SINGLE_OPTION -> this.buildSingleOptionInput(key, entry, componentMeta);
            };
            if (input != null) {
                inputs.add(input);
            }
        }
        return inputs;
    }

    private DialogInput buildBooleanInput(String key, ConfigFieldContext.ConfigFieldEntry entry, PaperMetaUpdater componentMeta) {
        var configOption = entry.configOption();
        boolean initialValue;
        try {
            Object val = entry.processor() != null ? entry.processor().getDisplayValue(entry.field()) : entry.field().get(null);
            initialValue = val instanceof Boolean b ? b : false;
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

    private DialogInput buildNumberRangeInput(String key, ConfigFieldContext.ConfigFieldEntry entry, PaperMetaUpdater componentMeta) {
        var configOption = entry.configOption();
        float initialValue;
        try {
            Object val = entry.processor() != null ? entry.processor().getDisplayValue(entry.field()) : entry.field().get(null);
            initialValue = val instanceof Number n ? n.floatValue() : (configOption.startRange() + configOption.endRange()) / 2;
        } catch (IllegalAccessException e) {
            initialValue = (configOption.startRange() + configOption.endRange()) / 2;
        }
        float min = configOption.startRange();
        float max = configOption.endRange();
        if (min > max) {
            float temp = min;
            min = max;
            max = temp;
        }
        if (initialValue < min) {
            initialValue = min;
        } else if (initialValue > max) {
            initialValue = max;
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

    private DialogInput buildTextInput(String key, ConfigFieldContext.ConfigFieldEntry entry, PaperMetaUpdater componentMeta) {
        var configOption = entry.configOption();
        String defaultText;
        try {
            Object val = entry.processor() != null ? entry.processor().getDisplayValue(entry.field()) : entry.field().get(null);
            defaultText = val != null ? val.toString() : "";
        } catch (IllegalAccessException e) {
            defaultText = "";
        }
        int maxLength = configOption.maxLength() < 0 ? Integer.MAX_VALUE : configOption.maxLength();
        return DialogInput.text(
                key,
                configOption.width(),
                componentMeta.getComponent(configOption.label()),
                configOption.labelVisible(),
                defaultText,
                maxLength,
                null
        );
    }

    private DialogInput buildSingleOptionInput(String key, ConfigFieldContext.ConfigFieldEntry entry, PaperMetaUpdater componentMeta) {
        Field field = entry.field();
        var configOption = entry.configOption();
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
            Object val = entry.processor() != null ? entry.processor().getDisplayValue(field) : field.get(null);
            currentValue = val instanceof Enum<?> e ? e.name() : (val != null ? val.toString() : "");
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
