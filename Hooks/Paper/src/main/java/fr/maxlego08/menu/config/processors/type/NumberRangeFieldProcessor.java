package fr.maxlego08.menu.config.processors.type;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.config.ConfigFieldContext;
import fr.maxlego08.menu.config.processors.AbstractConfigFieldProcessor;

import java.lang.reflect.Field;

public class NumberRangeFieldProcessor extends AbstractConfigFieldProcessor {
    @Override
    public void processField(Field field, ConfigOption configOption, ConfigFieldContext context) {
        this.validateField(field, configOption);

        InputButton inputButton = this.createBaseInputButton(configOption, DialogInputType.NUMBER_RANGE);
//TODO: refaire
//         inputButton.setStart(configOption.startRange());
//         inputButton.setEnd(configOption.endRange());
//         inputButton.setStep(configOption.stepRange());

        String key = configOption.key();
        Class<?> fieldType = field.getType();

        if (fieldType == int.class || fieldType == Integer.class) {
            this.setupIntegerField(field, inputButton, context, key);
        } else if (fieldType == long.class || fieldType == Long.class) {
            this.setupLongField(field, inputButton, context, key);
        } else if (fieldType == float.class || fieldType == Float.class) {
            this.setupFloatField(field, inputButton, context, key);
        } else if (fieldType == double.class || fieldType == Double.class) {
            this.setupDoubleField(field, inputButton, context, key);
        }

        context.addInputButton(inputButton);
    }

    private void setupIntegerField(Field field, InputButton inputButton, ConfigFieldContext context, String key) {
//         inputButton.setInitialValueRangeSupplier(() -> {
//             try {
//                 return (float) field.getInt(null);
//             } catch (IllegalAccessException e) {
//                 throw this.createReflectionException("get int value", field.getName(), e);
//             }
//         });

        context.addIntegerConsumer(key, value -> {
            try {
                field.setInt(null, value);
            } catch (IllegalAccessException e) {
                throw this.createReflectionException("set int value", field.getName(), e);
            }
        });
    }

    private void setupLongField(Field field, InputButton inputButton, ConfigFieldContext context, String key) {
//         inputButton.setInitialValueRangeSupplier(() -> {
//             try {
//                 return (float) field.getLong(null);
//             } catch (IllegalAccessException e) {
//                 throw this.createReflectionException("get long value", field.getName(), e);
//             }
//         });

        context.addIntegerConsumer(key, value -> {
            try {
                field.setLong(null, value);
            } catch (IllegalAccessException e) {
                throw this.createReflectionException("set long value", field.getName(), e);
            }
        });
    }

    private void setupFloatField(Field field, InputButton inputButton, ConfigFieldContext context, String key) {
//         inputButton.setInitialValueRangeSupplier(() -> {
//             try {
//                 return field.getFloat(null);
//             } catch (IllegalAccessException e) {
//                 throw this.createReflectionException("get float value", field.getName(), e);
//             }
//         });

        context.addFloatConsumer(key, value -> {
            try {
                field.setFloat(null, value);
            } catch (IllegalAccessException e) {
                throw this.createReflectionException("set float value", field.getName(), e);
            }
        });
    }

    private void setupDoubleField(Field field, InputButton inputButton, ConfigFieldContext context, String key) {
//         inputButton.setInitialValueRangeSupplier(() -> {
//             try {
//                 return (float) field.getDouble(null);
//             } catch (IllegalAccessException e) {
//                 throw this.createReflectionException("get double value", field.getName(), e);
//             }
//         });

        context.addFloatConsumer(key, value -> {
            try {
                field.setDouble(null, value.doubleValue());
            } catch (IllegalAccessException e) {
                throw this.createReflectionException("set double value", field.getName(), e);
            }
        });
    }

    @Override
    protected void validateField(Field field, ConfigOption configOption) {
        Class<?> fieldType = field.getType();
        if (!this.isNumericType(fieldType)) {
            throw new IllegalArgumentException("Field " + field.getName() + " must be numeric type for NUMBER_RANGE input");
        }
    }

    private boolean isNumericType(Class<?> type) {
        return type == int.class || type == Integer.class ||
                type == long.class || type == Long.class ||
                type == float.class || type == Float.class ||
                type == double.class || type == Double.class;
    }
}
