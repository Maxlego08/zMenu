package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.List;

public class OpenBookAction extends ActionHelper {

    private final String title;
    private final String author;
    private final List<String> lines;

    public OpenBookAction(String title, String author, List<String> lines) {
        this.title = title;
        this.author = author;
        this.lines = lines;
    }

    @Override
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        inventory.getPlugin().getMetaUpdater().openBook(player, papi(title, player), papi(author, player), papi(lines, player));
    }
}
