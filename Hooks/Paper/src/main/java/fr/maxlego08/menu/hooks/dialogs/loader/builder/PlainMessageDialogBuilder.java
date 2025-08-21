package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlainMessageDialogBuilder implements DialogBuilder{
    @Override
    public DialogBodyType getBodyType() {
        return DialogBodyType.PLAIN_MESSAGE;
    }

    @Override
    public DialogBody build(Player player, BodyButton button) {
        PaperComponent paperComponent = PaperComponent.getInstance();
        List<String> messages = button.getMessages();
        if (messages.isEmpty()) {
            return null;
        }
        List<Component> components = new ArrayList<>();
        for (String message : messages) {
            String parsedMessage = PlaceholderAPI.setPlaceholders(player, message);
            components.add(paperComponent.getComponent(parsedMessage));
        }
        Component finalComponent;
        if (components.size() == 1) {
            finalComponent = components.getFirst();
        } else {
            finalComponent = Component.join(Component.newline(), components);
        }
        return DialogBody.plainMessage(finalComponent, button.getMessageWidth());
    }
}
