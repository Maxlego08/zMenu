package fr.maxlego08.menu.api.button.buttons.dialogs.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogPlainMessageBody extends VanillaDialogBody {
    private final List<String> messages;

    public DialogPlainMessageBody(List<String> messages, int width) {
        super(DialogBodyType.PLAIN_MESSAGE, width);
        this.messages = messages;
    }

    @Override
    public DialogBody build(@NotNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        PaperMetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin plugin = context.getPlugin();

        if (this.messages.isEmpty()) {
            return null;
        }
        List<Component> components = new ArrayList<>();
        for (String message : this.messages) {
            String parsedMessage = plugin.parse(player, placeholders.parse(message));
            components.add(metaUpdater.getComponent(parsedMessage));
        }
        Component finalComponent;
        if (components.size() == 1) {
            finalComponent = components.getFirst();
        } else {
            finalComponent = Component.join(JoinConfiguration.newlines(), components);
        }
        return DialogBody.plainMessage(finalComponent, this.width);
    }
}
