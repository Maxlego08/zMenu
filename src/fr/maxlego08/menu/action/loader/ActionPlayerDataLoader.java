package fr.maxlego08.menu.action.loader;

import fr.maxlego08.menu.action.ZActionPlayerData;
import fr.maxlego08.menu.api.action.data.ActionPlayerData;
import fr.maxlego08.menu.api.action.data.ActionPlayerDataType;
import fr.maxlego08.menu.exceptions.InventoryException;
import fr.maxlego08.menu.zcore.utils.loader.Loader;
import org.bukkit.configuration.file.YamlConfiguration;

public class ActionPlayerDataLoader implements Loader<ActionPlayerData> {

    @Override
    public ActionPlayerData load(YamlConfiguration configuration, String path, Object... objects)
            throws InventoryException {

        ActionPlayerDataType type = ActionPlayerDataType.valueOf(configuration.getString(path + "type", "SET"));
        String key = configuration.getString(path + "key");
        Object object = configuration.get(path + "value", true);
        long seconds = configuration.getLong(path + "seconds", 0);

        return new ZActionPlayerData(key, type, object, seconds);
    }

    @Override
    public void save(ActionPlayerData object, YamlConfiguration configuration, String path, Object... objects) {

    }

}
