package fr.maxlego08.menu.button.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.button.buttons.PaginationPreviousButton;

public class PaginationPreviousButtonLoader extends AbstractPaginationButtonLoader {

    public PaginationPreviousButtonLoader(MenuPlugin plugin) {
        super(plugin, "pagination_previous");
    }

    @Override
    protected Button createButton(String contextId, boolean onlyRefreshButton) {
        return new PaginationPreviousButton((MenuPlugin) this.plugin, contextId, onlyRefreshButton);
    }
}
