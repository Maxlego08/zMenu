package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;

import java.util.ArrayList;
import java.util.List;

public class BodyButton extends Button {
    private DialogBodyType bodyType;

    // Item-specific properties
    private int width = 256; // Default width
    private int height = 100; // Default height for items
    private boolean showDecorations = true;
    private boolean showTooltip = true;
    private List<String> descriptionMessages = new ArrayList<>();
    private int descriptionWidth = 300;

    private int messageWidth = 300;

    public DialogBodyType getBodyType() {
        return bodyType;
    }

    public BodyButton setBodyType(DialogBodyType bodyType) {
        this.bodyType = bodyType;
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
}