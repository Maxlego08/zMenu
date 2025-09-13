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

    public void setBodyType(DialogBodyType bodyType) {
        this.bodyType = bodyType;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = Math.max(1, Math.min(width, bodyType == DialogBodyType.ITEM ? 256 : 1024));
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = Math.max(1, Math.min(height, 256));
    }

    public boolean isShowDecorations() {
        return showDecorations;
    }

    public void setShowDecorations(boolean showDecorations) {
        this.showDecorations = showDecorations;
    }

    public boolean isShowTooltip() {
        return showTooltip;
    }

    public void setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
    }

    public List<String> getDescriptionMessages() {
        return descriptionMessages;
    }

    public void setDescriptionMessages(List<String> descriptionMessages) {
        this.descriptionMessages = descriptionMessages != null ? descriptionMessages : new ArrayList<>();
    }

    public int getDescriptionWidth() {
        return descriptionWidth;
    }

    public void setDescriptionWidth(int descriptionWidth) {
        this.descriptionWidth = Math.max(1, Math.min(descriptionWidth, 1024));
    }

    public int getMessageWidth() {
        return messageWidth;
    }

    public void setMessageWidth(int messageWidth) {
        this.messageWidth = Math.max(1, Math.min(messageWidth, 1024));
    }
}