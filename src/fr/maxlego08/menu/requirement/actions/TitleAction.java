package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.meta.Meta;
import org.bukkit.entity.Player;

public class TitleAction extends Action {

    private final String title;
    private final String subtitle;
    private final long start;
    private final long duration;
    private final long end;

    public TitleAction(String title, String subtitle, long start, long duration, long end) {
        this.title = title;
        this.subtitle = subtitle;
        this.start = start;
        this.duration = duration;
        this.end = end;
    }

    @Override
    protected void execute(Player player, Button button, InventoryDefault inventory, Placeholders placeholders) {
        Meta.meta.sendTitle(player, this.papi(placeholders.parse(title), player), this.papi(placeholders.parse(subtitle), player), start, duration, end);
    }

}
