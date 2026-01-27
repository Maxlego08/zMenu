package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

public class TitleAction extends ActionHelper {

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
    protected void execute(@NonNull Player player, Button button, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        inventory.getPlugin().getMetaUpdater().sendTitle(player, this.papi(placeholders.parse(title), player), this.papi(placeholders.parse(subtitle), player), start, duration, end);
    }

}
