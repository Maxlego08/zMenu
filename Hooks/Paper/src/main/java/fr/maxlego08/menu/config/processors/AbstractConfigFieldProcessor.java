package fr.maxlego08.menu.config.processors;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.DialogInputType;

import java.lang.reflect.Field;

public abstract class AbstractConfigFieldProcessor implements ConfigFieldProcessor {

    protected InputButton createBaseInputButton(ConfigOption configOption, DialogInputType type) {
        InputButton inputButton = new InputButton();
        inputButton.setInputType(type);
        inputButton.setLabel(configOption.label());
        inputButton.setLabelVisible(configOption.labelVisible());
        inputButton.setKey(configOption.key());
        return inputButton;
    }

    protected RuntimeException createReflectionException(String operation, String fieldName, Exception cause) {
        return new RuntimeException("Failed to " + operation + " on field: " + fieldName, cause);
    }

    protected void validateField(Field field, ConfigOption configOption) {
    }
}
