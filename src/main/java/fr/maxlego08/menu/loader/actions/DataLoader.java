package fr.maxlego08.menu.loader.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoActionLoader;
import fr.maxlego08.menu.api.loader.ActionLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.data.ActionPlayerDataType;
import fr.maxlego08.menu.api.utils.TypedMapAccessor;
import fr.maxlego08.menu.api.variable.VariableScope;
import fr.maxlego08.menu.loader.ActionPlayerDataLoader;
import fr.maxlego08.menu.requirement.ZActionPlayerData;
import fr.maxlego08.menu.requirement.actions.DataAction;
import org.jspecify.annotations.NonNull;

import java.io.File;
import java.util.Locale;

@AutoActionLoader
public class DataLoader extends ActionLoader {

    private final MenuPlugin plugin;

    public DataLoader(MenuPlugin plugin) {
        super("data");
        this.plugin = plugin;
    }

    @Override
    public Action load(@NonNull String path, @NonNull TypedMapAccessor accessor, @NonNull File file) {
        ActionPlayerDataType type = ActionPlayerDataType.valueOf(accessor.getString("action", "SET").toUpperCase(Locale.ROOT));
        String key = accessor.getString("key");
        Object object = accessor.getObject("value", true);
        String seconds = accessor.getString("seconds");
        if (seconds == null) {
            seconds = String.valueOf(accessor.getLong("seconds", 0L));
        }
        boolean mathExpression = accessor.getBoolean("math", false);
        VariableScope scope = ActionPlayerDataLoader.readScope(accessor.getString("scope", "player"), path + "scope");
        return new DataAction(new ZActionPlayerData(this.plugin, key, type, object, seconds, mathExpression, scope), this.plugin);
    }
}
