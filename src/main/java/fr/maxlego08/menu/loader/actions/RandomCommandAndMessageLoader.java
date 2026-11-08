package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.actions.CommandAndMessageAction;
import fr.maxlego08.menu.requirement.actions.RandomCommandAndMessageAction;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.ConfigurationSection;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Reads every section of the action (1, 2, ...) as an entry with commands and messages.
 * The options set on the action itself are the defaults of every entry.
 */
public abstract class RandomCommandAndMessageLoader extends ActionLoader {

    private final String serializedType;

    protected RandomCommandAndMessageLoader(String serializedType, String... names) {
        super(names);
        this.serializedType = serializedType;
    }

    protected abstract CommandAndMessageAction loadEntry(TypedMapAccessor entry, TypedMapAccessor parent);

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        Map<String, CommandAndMessageAction> entries = new LinkedHashMap<>();
        for (Map.Entry<String, Object> element : accessor.map().entrySet()) {
            Map<String, Object> section = toMap(element.getValue());
            if (section == null) continue;
            TypedMapAccessor entry = new TypedMapAccessor(section);
            if (!entry.contains("commands") && !entry.contains("messages") && !entry.contains("message")) continue;
            entries.put(element.getKey(), this.loadEntry(entry, accessor));
        }

        if (entries.isEmpty()) {
            Logger.info("The action " + this.serializedType + " in " + file.getPath() + " (" + path + ") has no entry with commands or messages.", Logger.LogType.WARNING);
        }
        return new RandomCommandAndMessageAction(this.serializedType, entries);
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> toMap(Object value) {
        if (value instanceof ConfigurationSection section) return section.getValues(false);
        if (value instanceof Map<?, ?> map) {
            Map<String, Object> result = new LinkedHashMap<>();
            map.forEach((k, v) -> result.put(String.valueOf(k), v));
            return result;
        }
        return null;
    }

    protected static boolean getBoolean(TypedMapAccessor entry, TypedMapAccessor parent, boolean defaultValue, String... keys) {
        for (String key : keys) {
            if (entry.contains(key)) return entry.getBoolean(key);
        }
        for (String key : keys) {
            if (parent.contains(key)) return parent.getBoolean(key);
        }
        return defaultValue;
    }
}
