package fr.maxlego08.menu.hooks.dialogs.requirement.actions;

import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.action.DialogAction;
import io.papermc.paper.dialog.DialogResponseView;
import net.kyori.adventure.audience.Audience;
import org.bukkit.entity.Player;

import java.util.List;

public class MessageAction extends DialogAction {
    private final List<String> message;

    public MessageAction(final List<String> message) {
        this.message = message;
    }

    @Override
    public void execute(DialogResponseView view, Audience audience, Placeholders placeholders, Player player) {
        PaperComponent component = PaperComponent.getInstance();
        for (String message : message) {
            player.sendMessage(component.getComponent(parse(message, player, placeholders)));
        }
    }
}
