package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.DefaultButtonValue;
import fr.maxlego08.menu.api.loader.ButtonLoader;
import fr.maxlego08.menu.button.buttons.PaginationNextButton;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PaginationNextButtonLoader extends ButtonLoader {

    public PaginationNextButtonLoader(MenuPlugin plugin) {
        super(plugin, "pagination_next");
    }

    @Override
    public @Nullable Button load(@NotNull YamlConfiguration configuration, @NotNull String path, @NotNull DefaultButtonValue defaultButtonValue) {
        String contextId = configuration.getString(path + "context-id");
        if (contextId == null) {
            Logger.info("Context-id is required for pagination_next button at path: " + path);
            return null;
        }
        return new PaginationNextButton((MenuPlugin) this.plugin, contextId);
    }
}