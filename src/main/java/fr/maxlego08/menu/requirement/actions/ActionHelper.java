package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public abstract class ActionHelper extends Action {

    protected String papi(String string, Player player) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string);
    }

    protected List<String> papi(List<String> strings, Player player) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings);
    }

}
