package fr.maxlego08.menu.config.processors;

import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.config.ConfigFieldContext;

import java.lang.reflect.Field;

/**
 * Interface for processing specific field types in configuration classes
 */
public interface ConfigFieldProcessor {

    /**
     * Process a field and add the corresponding input button and consumer to the context
     * @param field The field to process
     * @param configOption The ConfigOption annotation
     * @param context The context to add the processed data to
     */
    void processField(Field field, ConfigOption configOption, ConfigFieldContext context);
}