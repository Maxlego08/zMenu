package fr.maxlego08.menu.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.exceptions.InventoryException;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerData;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.utils.Loader;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jspecify.annotations.NonNull;

import java.util.Arrays;
import java.util.Locale;

public class ActionPlayerDataLoader implements Loader<ActionPlayerData> {

    private final MenuPlugin plugin;

    public ActionPlayerDataLoader(MenuPlugin plugin) {
        this.plugin = plugin;
    }

    /**
     * Reads the {@code scope} of a data action: {@code player} (default), {@code server} or
     * {@code global}. An unknown scope is reported and read as {@code player}.
     */
    public static VariableScope readScope(String value, String path) {
        VariableScope scope = VariableScope.parse(value);
        if (scope == null) {
            Logger.info("Data scope " + value + " is not valid at " + path + ", expected one of player, server, global", Logger.LogType.ERROR);
            return VariableScope.PLAYER;
        }
        return scope;
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
        VariableScope scope = readScope(configuration.getString(path + "scope", "player"), path + "scope");

        return new ZActionPlayerData(this.plugin, key, type, object, seconds, mathExpression, scope);
    }

}
