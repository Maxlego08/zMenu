package fr.maxlego08.menu.hooks.dialogs.loader.builder;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.hooks.dialogs.DialogManager;
import fr.maxlego08.menu.hooks.dialogs.buttons.BodyButton;
import fr.maxlego08.menu.api.enums.DialogBodyType;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import io.papermc.paper.registry.data.dialog.body.PlainMessageDialogBody;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class ItemDialogBuilder implements DialogBuilder{
    private final DialogManager dialogManager;

    public ItemDialogBuilder(DialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    @Override
    public DialogBodyType getBodyType() {
        return DialogBodyType.ITEM;
    }

    @Override
    public DialogBody build(Player player, BodyButton button) {
        MenuItemStack menuItemStack = button.getItemStack();
        if (menuItemStack == null) {
            return null;
        }

        ItemStack item = menuItemStack.build(player, button.isUseCache());
        if (item == null) {
            return null;
        }
        List<String> descriptionMessages = button.getDescriptionMessages();
        PlainMessageDialogBody description = null;
        if (!descriptionMessages.isEmpty()) {
            List<Component> descComponents = new ArrayList<>();
            for (String descMessage : descriptionMessages) {
                String parsedMessage = PlaceholderAPI.setPlaceholders(player, descMessage);
                descComponents.add(this.dialogManager.getPaperComponent().getComponent(parsedMessage));
            }

            Component finalDescComponent;
            if (descComponents.size() == 1) {
                finalDescComponent = descComponents.getFirst();
            } else {
                finalDescComponent = Component.join(Component.newline(), descComponents);
            }

            description = DialogBody.plainMessage(finalDescComponent, button.getDescriptionWidth());
        }

        return DialogBody.item(item)
                .description(description)
                .showDecorations(button.isShowDecorations())
                .showTooltip(button.isShowTooltip())
                .width(button.getWidth())
                .height(button.getHeight())
                .build();
    }
}
