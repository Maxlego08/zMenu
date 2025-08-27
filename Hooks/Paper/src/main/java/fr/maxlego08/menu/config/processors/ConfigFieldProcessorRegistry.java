package fr.maxlego08.menu.config.processors;

import fr.maxlego08.menu.api.enums.DialogInputType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

/**
 * Registry for ConfigFieldProcessor implementations
 */
public class ConfigFieldProcessorRegistry {
    private final Map<DialogInputType, ConfigFieldProcessor> processors = new EnumMap<>(DialogInputType.class);

    /**
     * Register a processor for a specific input type
     * @param inputType The input type
     * @param processor The processor
     */
    public void registerProcessor(DialogInputType inputType, ConfigFieldProcessor processor) {
        processors.put(inputType, processor);
    }

    /**
     * Get a processor for a specific input type
     * @param inputType The input type
     * @return The processor, or null if not found
     */
    public ConfigFieldProcessor getProcessor(DialogInputType inputType) {
        return processors.get(inputType);
    }

    /**
     * Get a processor for a specific input type as Optional
     * @param inputType The input type
     * @return The processor wrapped in Optional
     */
    public Optional<ConfigFieldProcessor> getProcessorOptional(DialogInputType inputType) {
        return Optional.ofNullable(processors.get(inputType));
    }

    /**
     * Check if a processor is registered for the given input type
     * @param inputType The input type
     * @return true if a processor is registered
     */
    public boolean hasProcessor(DialogInputType inputType) {
        return processors.containsKey(inputType);
    }

    /**
     * Remove a processor for a specific input type
     * @param inputType The input type
     */
    public void unregisterProcessor(DialogInputType inputType) {
        processors.remove(inputType);
    }

    /**
     * Clear all registered processors
     */
    public void clear() {
        processors.clear();
    }
}