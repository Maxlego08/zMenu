package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.button.buttons.PaginationNextButton;

@AutoButtonLoader
public class PaginationNextButtonLoader extends AbstractPaginationButtonLoader {

    public PaginationNextButtonLoader(MenuPlugin plugin) {
        super(plugin, "pagination_next");
    }

    @Override
    protected Button createButton(String contextId, boolean onlyRefreshButton) {
        return new PaginationNextButton((MenuPlugin) this.plugin, contextId, onlyRefreshButton);
    }
}
