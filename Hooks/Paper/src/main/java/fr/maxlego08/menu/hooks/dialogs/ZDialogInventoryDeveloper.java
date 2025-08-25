package fr.maxlego08.menu.hooks.dialogs;

import fr.maxlego08.menu.api.MenuPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class ZDialogInventoryDeveloper extends ZDialogInventory{
    private final Map<String, Consumer<Boolean>> consumerMap = new HashMap<>();

    public ZDialogInventoryDeveloper(MenuPlugin plugin, String name, String fileName, String externalTitle) {
        super(plugin, name, fileName, externalTitle);
    }

    public Map<String, Consumer<Boolean>> getConsumerMap() {
        return consumerMap;
    }

    public void setConsumerMap(Map<String, Consumer<Boolean>> consumerMap) {
        this.consumerMap.clear();
        this.consumerMap.putAll(consumerMap);
    }
}
