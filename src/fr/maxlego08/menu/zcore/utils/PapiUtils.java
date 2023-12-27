package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class PapiUtils extends TranslationHelper {

    private static volatile Placeholder placeholder;

    private Placeholder use() {
        if (placeholder == null) {
            placeholder = Placeholder.Placeholders.getPlaceholder();
        }
        return placeholder;
    }

    /**
     * @param placeHolder string
     * @param player      The player
     * @return string
     */
    public String papi(String placeHolder, Player player) {
        // If the text does not contain a placeholder, then nothing is done
        if (!placeHolder.contains("%")) return placeHolder;
        return this.use().setPlaceholders(player, placeHolder).replace("%player%", player.getName());
    }

    /**
     * Transforms a list into a list with placeholder API
     *
     * @param placeHolders list of string
     * @param player       The player
     * @return placeholders
     */
    public List<String> papi(List<String> placeHolders, Player player) {
        return placeHolders.stream().map(placeHolder -> papi(placeHolder, player)).collect(Collectors.toList());
    }
}