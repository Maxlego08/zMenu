package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

public class PapiUtils extends TranslationHelper {

    private transient final Placeholder placeholder = Placeholder.Placeholders.getPlaceholder();

    /**
     * @param placeHolder
     * @param player
     * @return string
     */
    public String papi(String placeHolder, Player player) {
        return placeholder.setPlaceholders(player, placeHolder);
    }

    /**
     * Transforms a list into a list with placeholder API
     *
     * @param placeHolder
     * @param player
     * @return placeholders
     */
    public List<String> papi(List<String> placeHolder, Player player) {
        return placeholder.setPlaceholders(player, placeHolder);
    }

}