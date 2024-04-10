package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.entity.Player;

import java.util.List;

public class OpenBookAction extends Action {

    private final String title;
    private final String author;
    private final List<String> lines;

    public OpenBookAction(String title, String author, List<String> lines) {
        this.title = title;
        this.author = author;
        this.lines = lines;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        Meta.meta.openBook(player, papi(title, player), papi(author, player), papi(lines, player));
    }
}
