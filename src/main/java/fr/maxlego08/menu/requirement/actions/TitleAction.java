package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.common.utils.ActionHelper;
import org.bukkit.entity.Player;
import org.jspecify.annotations.NonNull;

import java.util.Map;

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
        inventory.getPlugin().getMetaUpdater().sendTitle(player, this.papi(placeholders.parse(this.title), player), this.papi(placeholders.parse(this.subtitle), player), this.start, this.duration, this.end);
    }

    @Override
    protected void serializeProperties(@NonNull Map<String, Object> map) {
        map.put("type", "title");
        map.put("title", this.title);
        map.put("subtitle", this.subtitle);
        if (this.start != 0) map.put("start", this.start);
        if (this.duration != 0) map.put("duration", this.duration);
        if (this.end != 0) map.put("end", this.end);
    }

}
