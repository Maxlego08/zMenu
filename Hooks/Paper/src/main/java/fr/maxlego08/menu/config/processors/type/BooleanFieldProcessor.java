package fr.maxlego08.menu.config.processors.type;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.config.ConfigFieldContext;
import fr.maxlego08.menu.config.processors.AbstractConfigFieldProcessor;

import java.lang.reflect.Field;

public class BooleanFieldProcessor extends AbstractConfigFieldProcessor {
    @Override
    public void processField(Field field, ConfigOption configOption, ConfigFieldContext context) {
        validateField(field, configOption);

        InputButton inputButton = createBaseInputButton(configOption, DialogInputType.BOOLEAN);

        inputButton.setInitialValueSupplier(() -> {
            try {
                return field.getBoolean(null);
            } catch (IllegalAccessException e) {
                throw createReflectionException("get boolean value", field.getName(), e);
            }
        });

        inputButton.setTextTrue(configOption.trueText());
        inputButton.setTextFalse(configOption.falseText());

        String key = configOption.key();
        context.addBooleanConsumer(key, value -> {
            try {
                field.setBoolean(null, value);
            } catch (IllegalAccessException e) {
                throw createReflectionException("set boolean value", field.getName(), e);
            }
        });

        context.addInputButton(inputButton);
    }

    @Override
    protected void validateField(Field field, ConfigOption configOption) {
        Class<?> fieldType = field.getType();
        if (fieldType != boolean.class && fieldType != Boolean.class) {
            throw new IllegalArgumentException("Field " + field.getName() + " must be boolean type for BOOLEAN input");
        }
    }
}
