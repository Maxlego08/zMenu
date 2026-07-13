package fr.maxlego08.menu.common.utils;

import fr.maxlego08.menu.api.placeholder.Placeholder;
import fr.maxlego08.menu.api.requirement.Action;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ActionHelper extends Action {

    protected String papi(String string, Player player) {
        if (string == null || !string.contains("%")) return string;
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string).replace("\uF000", "%");
    }

    protected List<String> papi(List<String> strings, Player player) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings).stream()
                .map(s -> s.replace("\uF000", "%"))
                .toList();
    }

}
