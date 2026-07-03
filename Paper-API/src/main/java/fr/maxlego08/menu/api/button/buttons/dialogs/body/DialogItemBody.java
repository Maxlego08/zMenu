package fr.maxlego08.menu.api.button.buttons.dialogs.body;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.JoinConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogItemBody extends VanillaDialogBody {
    private final @NotNull List<String> descriptionMessages;
    private final int descriptionWidth;
    private final boolean showDecorations;
    private final boolean showTooltip;
    private final int height;

    public DialogItemBody(
            @NotNull List<@NotNull String> descriptionMessages,
            int descriptionWidth,
            boolean showDecoration,
            boolean showTooltip,
            int width,
            int height
    ) {
        super(DialogBodyType.ITEM, width);
        this.descriptionMessages = descriptionMessages;
        this.descriptionWidth = descriptionWidth;
        this.showDecorations = showDecoration;
        this.showTooltip = showTooltip;
        this.height = height;
    }

    @Override
    public DialogBody build(@NotNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        PaperMetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin plugin = context.getPlugin();

        MenuItemStack menuItemStack = this.getItemStack();
        if (menuItemStack == null) {
            return null;
        }
        ItemStack itemStack = menuItemStack.build(player, this.isUseCache(), placeholders);
        if (itemStack == null) {
            return null;
        }
        PlainMessageDialogBody description = null;
        if (!this.descriptionMessages.isEmpty()) {
            List<Component> descComponents = new ArrayList<>();
            for (String descMessage : this.descriptionMessages) {
                String parsedMessage = plugin.parse(player, placeholders.parse(descMessage));
                descComponents.add(metaUpdater.getComponent(parsedMessage));
            }

            Component finalDescComponent;
            if (descComponents.size() == 1) {
                finalDescComponent = descComponents.getFirst();
            } else {
                finalDescComponent = Component.join(JoinConfiguration.newlines(), descComponents);

            }

            description = DialogBody.plainMessage(finalDescComponent, this.descriptionWidth);
        }
        return DialogBody.item(
                itemStack,
                description,
                this.showDecorations,
                this.showTooltip,
                this.width,
                this.height
        );
    }
}
