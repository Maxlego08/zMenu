package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class PlainMessageDialogBuilder implements DialogBuilder{
    private final ZDialogManager dialogManager;
    private final MenuPlugin menuPlugin;

    public PlainMessageDialogBuilder(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.dialogManager = dialogManager;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogBodyType getBodyType() {
        return DialogBodyType.PLAIN_MESSAGE;
    }

    @Override
    public DialogBody build(Player player, BodyButton button) {
        List<String> messages = button.getMessages();
        if (messages.isEmpty()) {
            return null;
        }
        List<Component> components = new ArrayList<>();
        for (String message : messages) {
            String parsedMessage = this.menuPlugin.parse(player, message);
            components.add(this.dialogManager.getPaperComponent().getComponent(parsedMessage));
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
