package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractPaginationButtonLoader extends ButtonLoader {

    public AbstractPaginationButtonLoader(MenuPlugin plugin, String key) {
        super(plugin, key);
    }

    @Override
    public @Nullable Button load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull DefaultButtonValue defaultButtonValue) {
        String contextId = configuration.getString(path + "context-id");
        if (contextId == null) {
            Logger.info("Context-id is required for " + this.name + " button at path: " + path);
            return null;
        }
        boolean onlyRefreshButton = configuration.getBoolean(path + "only-refresh-button", false);
        return this.createButton(contextId, onlyRefreshButton);
    }

    protected abstract Button createButton(String contextId, boolean onlyRefreshButton);
}
