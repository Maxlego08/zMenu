package fr.maxlego08.menu.api.configuration;

import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

/**
 * Processor for handling custom types or overriding default types in configuration fields.
 */
public interface ConfigFieldProcessor {

    /**
     * Checks if this processor can handle the given field.
     *
     * @param field        The field to check.
     * @param configOption The config option annotation.
     * @return True if this processor should process the field.
     */
    boolean canProcess(@NotNull Field field, @NotNull ConfigOption configOption);

    /**
     * Resolves the DialogInputType for the field.
     *
     * @param field        The field.
     * @param configOption The config option annotation.
     * @return The resolved dialog input type.
     */
    @NotNull DialogInputType resolveInputType(@NotNull Field field, @NotNull ConfigOption configOption);

    /**
     * Sets the field value with the dialog value.
     *
     * @param field The field.
     * @param value The value from the dialog.
     * @throws IllegalAccessException If the field cannot be set.
     */
    void setFieldValue(@NotNull Field field, @NotNull Object value) throws IllegalAccessException;
}
