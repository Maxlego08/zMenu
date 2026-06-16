package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.hooks.dialogs.inventory.AbstractDialogInventory;
import io.papermc.paper.dialog.Dialog;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ZDialogInventoryDeveloper extends AbstractDialogInventory {
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
        return this.consumerMap;
    }

    public void setConsumerMap(Map<String, Consumer<Boolean>> consumerMap) {
        this.consumerMap.clear();
        this.consumerMap.putAll(consumerMap);
    }

    public Map<String, Consumer<Float>> getFloatConsumerMap() {
        return this.floatConsumerMap;
    }

    public void setFloatConsumerMap(Map<String, Consumer<Float>> floatConsumerMap) {
        this.floatConsumerMap.clear();
        this.floatConsumerMap.putAll(floatConsumerMap);
    }

    public Map<String, Consumer<Integer>> getIntegerConsumerMap() {
        return this.integerConsumerMap;
    }

    public void setIntegerConsumerMap(Map<String, Consumer<Integer>> integerConsumerMap) {
        this.integerConsumerMap.clear();
        this.integerConsumerMap.putAll(integerConsumerMap);
    }

    public String getBooleanConfirmText() {
        return this.booleanConfirmText;
    }

    public void setBooleanConfirmText(String booleanConfirmText) {
        this.booleanConfirmText = booleanConfirmText;
    }

    public String getNumberRangeConfirmText() {
        return this.numberRangeConfirmText;
    }

    public void setNumberRangeConfirmText(String numberRangeConfirmText) {
        this.numberRangeConfirmText = numberRangeConfirmText;
    }

    public Map<String, Consumer<String>> getStringConsumerMap() {
        return this.stringConsumerMap;
    }
    public void setStringConsumerMap(Map<String, Consumer<String>> stringConsumerMap) {
        this.stringConsumerMap.clear();
        this.stringConsumerMap.putAll(stringConsumerMap);
    }
    public String getStringConfirmText() {
        return this.stringConfirmText;
    }

    public void setStringConfirmText(String stringConfirmText) {
        this.stringConfirmText = stringConfirmText;
    }

    public Consumer<Boolean> getUpdateConsumer() {
        return this.updateConsumer;
    }

    @Override
    public Dialog buildDialog(@NotNull Player player, @NotNull PaperMetaUpdater paperComponent) {
        return null;
    }
}
