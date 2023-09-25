package fr.maxlego08.menu.zcore.utils;

import fr.maxlego08.menu.placeholder.Placeholder;
import org.bukkit.entity.Player;

import java.util.List;

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
        return this.use().setPlaceholders(player, placeHolder);
    }

    /**
     * Transforms a list into a list with placeholder API
     *
     * @param placeHolders list of string
     * @param player       The player
     * @return placeholders
     */
    public List<String> papi(List<String> placeHolders, Player player) {
        return this.use().setPlaceholders(player, placeHolders);
    }
}