package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.PaginationPreviousButton;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaginationPreviousButtonLoader extends ButtonLoader {

    public PaginationPreviousButtonLoader(MenuPlugin plugin) {
        super(plugin, "pagination_previous");
    }

    @Override
    public @Nullable Button load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull DefaultButtonValue defaultButtonValue) {
        String contextId = configuration.getString(path + "context-id");
        if (contextId == null) {
            Logger.info("Context-id is required for pagination_previous button at path: " + path);
            return null;
        }
        return new PaginationPreviousButton((MenuPlugin) this.plugin, contextId);
    }
}