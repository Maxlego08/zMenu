package fr.maxlego08.menu.config.processors.type;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.config.ConfigFieldContext;
import fr.maxlego08.menu.config.processors.AbstractConfigFieldProcessor;

import java.lang.reflect.Field;

public class TextFieldProcessor extends AbstractConfigFieldProcessor {
    @Override
    public void processField(Field field, ConfigOption configOption, ConfigFieldContext context) {
        validateField(field, configOption);

        InputButton inputButton = createBaseInputButton(configOption, DialogInputType.TEXT);

        inputButton.setWidth(configOption.width());
        inputButton.setMaxLength(configOption.maxLength());

        int multilineMaxLines = configOption.multilineMaxLines();
        if (multilineMaxLines > 0) {
            inputButton.setMultilineMaxLines(multilineMaxLines);
        }

        int multilineHeight = configOption.multilineHeight();
        if (multilineHeight > 0) {
            inputButton.setMultilineHeight(multilineHeight);
        }

        inputButton.setDefaultTextSupplier(() -> {
            try {
                return (String) field.get(null);
            } catch (IllegalAccessException e) {
                throw createReflectionException("get string value", field.getName(), e);
            }
        });

        String key = configOption.key();
        context.addStringConsumer(key, value -> {
            try {
                field.set(null, value);
            } catch (IllegalAccessException e) {
                throw createReflectionException("set string value", field.getName(), e);
            }
        });

        context.addInputButton(inputButton);
    }

    @Override
    protected void validateField(Field field, ConfigOption configOption) {
        if (field.getType() != String.class) {
            throw new IllegalArgumentException("Field " + field.getName() + " must be String type for TEXT input");
        }
    }
}
