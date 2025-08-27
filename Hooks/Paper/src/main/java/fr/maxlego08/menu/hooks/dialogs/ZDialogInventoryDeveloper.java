package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ZDialogInventoryDeveloper extends ZDialogInventory{
    private final Map<String, Consumer<Boolean>> consumerMap = new HashMap<>();
    private final Map<String, Consumer<Float>> floatConsumerMap = new HashMap<>();
    private final Map<String, Consumer<Integer>> integerConsumerMap = new HashMap<>();
    private final Map<String, Consumer<String>> stringConsumerMap = new HashMap<>();
    private String booleanConfirmText = "%key%: %value%";
    private String numberRangeConfirmText = "%key%: %value%";
    private String stringConfirmText = "%key%: %text%";

    private final Consumer<Boolean> updateConsumer;

    public ZDialogInventoryDeveloper(MenuPlugin plugin, String name, String fileName, String externalTitle, Consumer<Boolean> updateConsumer) {
        super(plugin, name, fileName, externalTitle);
        this.updateConsumer = updateConsumer;
    }

    public Map<String, Consumer<Boolean>> getConsumerMap() {
        return consumerMap;
    }

    public void setConsumerMap(Map<String, Consumer<Boolean>> consumerMap) {
        this.consumerMap.clear();
        this.consumerMap.putAll(consumerMap);
    }

    public Map<String, Consumer<Float>> getFloatConsumerMap() {
        return floatConsumerMap;
    }

    public void setFloatConsumerMap(Map<String, Consumer<Float>> floatConsumerMap) {
        this.floatConsumerMap.clear();
        this.floatConsumerMap.putAll(floatConsumerMap);
    }

    public Map<String, Consumer<Integer>> getIntegerConsumerMap() {
        return integerConsumerMap;
    }

    public void setIntegerConsumerMap(Map<String, Consumer<Integer>> integerConsumerMap) {
        this.integerConsumerMap.clear();
        this.integerConsumerMap.putAll(integerConsumerMap);
    }

    public String getBooleanConfirmText() {
        return booleanConfirmText;
    }

    public void setBooleanConfirmText(String booleanConfirmText) {
        this.booleanConfirmText = booleanConfirmText;
    }

    public String getNumberRangeConfirmText() {
        return numberRangeConfirmText;
    }

    public void setNumberRangeConfirmText(String numberRangeConfirmText) {
        this.numberRangeConfirmText = numberRangeConfirmText;
    }

    public Map<String, Consumer<String>> getStringConsumerMap() {
        return stringConsumerMap;
    }
    public void setStringConsumerMap(Map<String, Consumer<String>> stringConsumerMap) {
        this.stringConsumerMap.clear();
        this.stringConsumerMap.putAll(stringConsumerMap);
    }
    public String getStringConfirmText() {
        return stringConfirmText;
    }

    public void setStringConfirmText(String stringConfirmText) {
        this.stringConfirmText = stringConfirmText;
    }

    public Consumer<Boolean> getUpdateConsumer() {
        return updateConsumer;
    }
}
