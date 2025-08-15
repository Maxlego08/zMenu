package fr.maxlego08.menu.hooks.dialogs.buttons;

import fr.maxlego08.menu.api.MenuItemStack;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.hooks.dialogs.enums.DialogBodyType;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilder;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderClass;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BodyButton extends PlaceholderButton {
    private MenuPlugin plugin;
    private DialogBodyType bodyType;
    private MenuItemStack itemStack;
    private boolean useCache = true;
    private List<String> messages = new ArrayList<>();

    // Item-specific properties
    private int width = 256; // Default width
    private int height = 100; // Default height for items
    private boolean showDecorations = true;
    private boolean showTooltip = true;
    private List<String> descriptionMessages = new ArrayList<>();
    private int descriptionWidth = 300;

    private int messageWidth = 300;

    public MenuItemStack getItemStack() {
        return itemStack;
    }

    public BodyButton setItemStack(MenuItemStack itemStack) {
        this.itemStack = itemStack;
        return this;
    }

    public List<String> getMessages() {
        return this.messages;
    }

    public BodyButton setMessages(List<String> messages) {
        this.messages = messages != null ? messages : new ArrayList<>();
        return this;
    }

    public DialogBodyType getBodyType() {
        return bodyType;
    }

    public BodyButton setBodyType(DialogBodyType bodyType) {
        this.bodyType = bodyType;
        return this;
    }

    public MenuPlugin getPlugin() {
        return plugin;
    }

    public BodyButton setPlugin(MenuPlugin plugin) {
        this.plugin = plugin;
        return this;
    }

    public boolean isUseCache() {
        return useCache;
    }

    public BodyButton setUseCache(boolean useCache) {
        this.useCache = useCache;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public BodyButton setWidth(int width) {
        this.width = Math.max(1, Math.min(width, bodyType == DialogBodyType.ITEM ? 256 : 1024));
        return this;
    }

    public int getHeight() {
        return height;
    }

    public BodyButton setHeight(int height) {
        this.height = Math.max(1, Math.min(height, 256));
        return this;
    }

    public boolean isShowDecorations() {
        return showDecorations;
    }

    public BodyButton setShowDecorations(boolean showDecorations) {
        this.showDecorations = showDecorations;
        return this;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public BodyButton setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }

    public List<String> getDescriptionMessages() {
        return descriptionMessages;
    }

    public BodyButton setDescriptionMessages(List<String> descriptionMessages) {
        this.descriptionMessages = descriptionMessages != null ? descriptionMessages : new ArrayList<>();
        return this;
    }

    public int getDescriptionWidth() {
        return descriptionWidth;
    }

    public BodyButton setDescriptionWidth(int descriptionWidth) {
        this.descriptionWidth = Math.max(1, Math.min(descriptionWidth, 1024));
        return this;
    }

    public int getMessageWidth() {
        return messageWidth;
    }

    public BodyButton setMessageWidth(int messageWidth) {
        this.messageWidth = Math.max(1, Math.min(messageWidth, 1024));
        return this;
    }

    public DialogBody build(Player player) {
        if (bodyType == null) {
            return null;
        }
        Optional<DialogBuilder> dialogBuilderOptional = DialogBuilderClass.getDialogBuilder(bodyType);
        if (dialogBuilderOptional.isEmpty()) {
            return null;
        }
        DialogBuilder dialogBuilder = dialogBuilderOptional.get();
        return dialogBuilder.build(player, this);
    }
}