package fr.maxlego08.menu.config;

import fr.maxlego08.menu.api.configuration.annotation.ConfigOption;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ConfigFieldContext {

    private final Map<String, ConfigFieldEntry> entries = new LinkedHashMap<>();
    private final Map<String, Consumer<Object>> consumers = new LinkedHashMap<>();
    private Consumer<Boolean> updateConsumer;

    public record ConfigFieldEntry(Field field, ConfigOption configOption, DialogInputType resolvedType) {
    }

    public void register(String key, Field field, ConfigOption configOption, DialogInputType resolvedType) {
        if (this.entries.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate config key: '" + key + "' in field " + field.getName());
        }
        this.entries.put(key, new ConfigFieldEntry(field, configOption, resolvedType));
    }

    public void registerConsumer(String key, Consumer<Object> consumer) {
        if (this.consumers.containsKey(key)) {
            throw new IllegalArgumentException("Duplicate consumer key: '" + key + "'");
        }
        this.consumers.put(key, consumer);
    }

    public ConfigFieldEntry getEntry(String key) {
        return this.entries.get(key);
    }

    public Consumer<Object> getConsumer(String key) {
        return this.consumers.get(key);
    }

    public Map<String, ConfigFieldEntry> getEntries() {
        return Collections.unmodifiableMap(this.entries);
    }

    public Map<String, Consumer<Object>> getConsumers() {
        return Collections.unmodifiableMap(this.consumers);
    }

    public Consumer<Boolean> getUpdateConsumer() {
        return this.updateConsumer;
    }

    public void setUpdateConsumer(Consumer<Boolean> updateConsumer) {
        this.updateConsumer = updateConsumer;
    }

    public boolean isEmpty() {
        return this.entries.isEmpty();
    }

    public int size() {
        return this.entries.size();
    }
}
