package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.api.utils.Loader;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ActionPlayerDataLoader implements Loader<ActionPlayerData> {

    private final StorageManager storageManager;

    public ActionPlayerDataLoader(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public ActionPlayerData load(YamlConfiguration configuration, String path, Object... objects)
            throws InventoryException {

        ActionPlayerDataType type = ActionPlayerDataType.valueOf(configuration.getString(path + "type", "SET"));
        String key = configuration.getString(path + "key");
        Object object = configuration.get(path + "value", true);
        long seconds = configuration.getLong(path + "seconds", 0);

        return new ZActionPlayerData(storageManager, key, type, object, seconds);
    }

    @Override
    public void save(ActionPlayerData object, YamlConfiguration configuration, String path, File file, Object... objects) {

        configuration.set(path + "type", "SET");
        configuration.set(path + "key", object.getKey());
        configuration.set(path + "value", object.getValue());
        configuration.set(path + "seconds", object.getSeconds());

        try {
            configuration.save(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
