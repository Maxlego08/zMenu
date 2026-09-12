package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.storage.StorageManager;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Locale;

public class ActionPlayerDataLoader implements Loader<ActionPlayerData> {

    private final StorageManager storageManager;

    public ActionPlayerDataLoader(StorageManager storageManager) {
        this.storageManager = storageManager;
    }

    @Override
    public ActionPlayerData load(@NonNull YamlConfiguration configuration, @NonNull String path, Object... objects)
            throws InventoryException {

        String typeString = configuration.getString(path + "type", "SET").toUpperCase(Locale.ROOT);
        ActionPlayerDataType type;
        try {
            type = ActionPlayerDataType.valueOf(typeString);
        } catch (IllegalArgumentException exception) {
            Logger.info("Data type " + typeString + " is not valid at " + path + "type, expected one of " + Arrays.toString(ActionPlayerDataType.values()), Logger.LogType.ERROR);
            type = ActionPlayerDataType.SET;
        }
        String key = configuration.getString(path + "key");
        Object object = configuration.get(path + "value", true);
        String seconds = configuration.getString(path + "seconds", null);
        if (seconds == null) {
            seconds = String.valueOf(configuration.getLong(path + "seconds",0));
        }

        boolean mathExpression = configuration.getBoolean(path + "math", false);

        return new ZActionPlayerData(this.storageManager, key, type, object, seconds, mathExpression);
    }

    @Override
    public void save(ActionPlayerData object, @NonNull YamlConfiguration configuration, @NonNull String path, File file, Object... objects) {

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
