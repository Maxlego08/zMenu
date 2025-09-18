package fr.maxlego08.menu.config;

import fr.maxlego08.menu.api.button.dialogs.InputButton;

import java.util.*;
import java.util.function.Consumer;

/**
 * Context class that holds all the processed data from config fields
 */
public class ConfigFieldContext {
    private final List<InputButton> inputButtons = new ArrayList<>();
    private final Map<String, Consumer<Boolean>> booleanConsumers = new HashMap<>();
    private final Map<String, Consumer<Float>> floatConsumers = new HashMap<>();
    private final Map<String, Consumer<Integer>> integerConsumers = new HashMap<>();
    private final Map<String, Consumer<String>> stringConsumers = new HashMap<>();
    private final Map<String, Consumer<Long>> longConsumers = new HashMap<>();
    private Consumer<Boolean> updateConsumer;

    public void addInputButton(InputButton inputButton) {
        this.inputButtons.add(inputButton);
    }

    public void addBooleanConsumer(String key, Consumer<Boolean> consumer) {
        this.booleanConsumers.put(key, consumer);
    }

    public void addFloatConsumer(String key, Consumer<Float> consumer) {
        this.floatConsumers.put(key, consumer);
    }

    public void addIntegerConsumer(String key, Consumer<Integer> consumer) {
        this.integerConsumers.put(key, consumer);
    }

    public void addStringConsumer(String key, Consumer<String> consumer) {
        this.stringConsumers.put(key, consumer);
    }

    public void addLongConsumer(String key, Consumer<Long> consumer) {
        this.longConsumers.put(key, consumer);
    }

    public List<InputButton> getInputButtons() {
        return Collections.unmodifiableList(inputButtons);
    }

    public Map<String, Consumer<Boolean>> getBooleanConsumers() {
        return Collections.unmodifiableMap(booleanConsumers);
    }

    public Map<String, Consumer<Float>> getFloatConsumers() {
        return Collections.unmodifiableMap(floatConsumers);
    }

    public Map<String, Consumer<Integer>> getIntegerConsumers() {
        return Collections.unmodifiableMap(integerConsumers);
    }

    public Map<String, Consumer<String>> getStringConsumers() {
        return Collections.unmodifiableMap(stringConsumers);
    }

    public Map<String, Consumer<Long>> getLongConsumers() {
        return Collections.unmodifiableMap(longConsumers);
    }

    public boolean isEmpty() {
        return inputButtons.isEmpty();
    }

    public int size() {
        return inputButtons.size();
    }

    public Consumer<Boolean> getUpdateConsumer() {
        return updateConsumer;
    }

    public void setUpdateConsumer(Consumer<Boolean> updateConsumer) {
        this.updateConsumer = updateConsumer;
    }
}