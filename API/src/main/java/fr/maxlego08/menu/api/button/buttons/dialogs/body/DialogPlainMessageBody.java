package fr.maxlego08.menu.api.button.buttons.dialogs.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TemplateList;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogPlainMessageBody extends VanillaDialogBody {
    private final TemplateList messages;
    private final Map<String, TemplateList> localizedMessages;

    public DialogPlainMessageBody(List<String> messages, int width, @Nullable Map<String, List<String>> localizedMessages) {
        super(DialogBodyType.PLAIN_MESSAGE, width);
        this.messages = TextService.get().flow().compileList(messages);
        if (localizedMessages != null) {
            Map<String, TemplateList> localized = new HashMap<>(localizedMessages.size());
            localizedMessages.forEach((locale, lines) -> localized.put(locale, TextService.get().flow().compileList(lines)));
            this.localizedMessages = Map.copyOf(localized);
        } else {
            this.localizedMessages = Map.of();
        }
    }

    @Override
    public DialogBody build(@NotNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();

        List<Component> components = this.localizedMessages.getOrDefault(player.getLocale(), this.messages).render(placeholders.values(), player);

        Component finalComponent;
        if (components.size() == 1) {
            finalComponent = components.getFirst();
        } else {
            finalComponent = Component.join(JoinConfiguration.newlines(), components);
        }
        return DialogBody.plainMessage(finalComponent, this.width);
    }
}
