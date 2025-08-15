package fr.maxlego08.menu.hooks.dialogs.requirement.actions;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.audience.Audience;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class BroadcastAction extends DialogAction {
    private final List<String> message;

    public BroadcastAction(final List<String> message) {
        this.message = message;
    }

    @Override
    public void execute(DialogResponseView view, Audience audience, Placeholders placeholders, Player player) {
           Audience serverAudience = Audience.audience(Bukkit.getServer().getOnlinePlayers());
        PaperComponent component = PaperComponent.getInstance();
        for (String msg : message) {
            String parsedMessage = parse(msg, player, placeholders);
            serverAudience.sendMessage(component.getComponent(parsedMessage));
        }
    }
}
