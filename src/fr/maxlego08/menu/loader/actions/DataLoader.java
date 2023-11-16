package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.requirement.actions.DataAction;

import java.io.File;
import java.util.Map;

public class DataLoader implements ActionLoader {

    private final MenuPlugin plugin;

    public DataLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public String getKey() {
        return "data";
    }

    @Override
    public Action load(String path, Map<String, Object> map, File file) {
        ActionPlayerDataType type = ActionPlayerDataType.valueOf((String) map.getOrDefault(path + "type", "SET"));
        String key = (String) map.getOrDefault("key", null);
        Object object = map.getOrDefault("value", true);
        long seconds = Long.parseLong((String) map.getOrDefault("seconds", "1"));
        return new DataAction(new ZActionPlayerData(key, type, object, seconds), plugin);
    }
}
