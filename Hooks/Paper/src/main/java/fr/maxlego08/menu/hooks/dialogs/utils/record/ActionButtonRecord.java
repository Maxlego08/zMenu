package fr.maxlego08.menu.hooks.dialogs.utils.record;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

import java.util.List;

public record ActionButtonRecord(String label, String tooltip, int width, List<Action> actions) {
    public ActionButtonRecord parse(Player player) {
        return new ActionButtonRecord(parsePlaceholder(label,player), parsePlaceholder(tooltip,player), width, actions);
    }
    private String parsePlaceholder(String text, Player player) {
        return text.isEmpty() ? "" : PlaceholderAPI.setPlaceholders(player, text);
    }
}
