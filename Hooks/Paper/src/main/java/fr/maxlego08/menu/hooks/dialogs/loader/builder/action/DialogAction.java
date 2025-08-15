package fr.maxlego08.menu.hooks.dialogs.loader.builder.action;

import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.dialog.DialogResponseView;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

public abstract class DialogAction {

    public abstract void execute(DialogResponseView view, Audience audience, Placeholders placeholders, Player player);

    public String parse(String message, Player player, Placeholders placeholders){
        return papi(placeholders.parse(message), player);
    }

    public String parse(String message, Player player){return papi(message, player);}


    private String papi(String message, Player player) {
        return PlaceholderAPI.setPlaceholders(player, message);
    }
}
