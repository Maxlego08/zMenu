package fr.maxlego08.menu.hooks.dialogs.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class BuilderHelper {
    protected String papi(String string, Player player) {
        if (string == null || !string.contains("%")) return string;
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, string);
    }

    protected List<String> papi(List<String> strings, Player player) {
        return Placeholder.Placeholders.getPlaceholder().setPlaceholders(player, strings);
    }
}
