package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.requirement.actions.DataAction;

import java.io.File;

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
    public Action load(String path, TypedMapAccessor accessor, File file) {
        ActionPlayerDataType type = ActionPlayerDataType.valueOf(accessor.getString("type", "SET").toUpperCase());
        String key = accessor.getString("key");
        Object object = accessor.getObject("value", true);
        long seconds = accessor.getLong("seconds", 0L);
        return new DataAction(new ZActionPlayerData(key, type, object, seconds), plugin);
    }
}
