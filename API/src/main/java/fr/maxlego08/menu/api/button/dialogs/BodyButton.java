package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BodyButton extends Button {
    private final DialogBodyType bodyType;

    // Item-specific properties
    private int width = 256; // Default width
    private int height = 100; // Default height for items
    private boolean showDecorations = true;
    private boolean showTooltip = true;
    private @NotNull List<String> descriptionMessages = new ArrayList<>();
    private int descriptionWidth = 300;

    private int messageWidth = 300;

    public BodyButton(DialogBodyType bodyType) {
        this.bodyType = bodyType;
    }

    @Contract(pure = true)
    public DialogBodyType getBodyType() {
        return this.bodyType;
    }

    @Contract(pure = true)
    public int getWidth() {
        return this.width;
    }

    @Contract("_ -> this")
    public BodyButton setWidth(int width) {
        this.width = Math.max(1, Math.min(width, bodyType == DialogBodyType.ITEM ? 256 : 1024));
        return this;
    }

    @Contract(pure = true)
    public int getHeight() {
        return this.height;
    }

    @Contract("_ -> this")
    public BodyButton setHeight(int height) {
        this.height = Math.max(1, Math.min(height, 256));
        return this;
    }

    @Contract(pure = true)
    public boolean isShowDecorations() {
        return this.showDecorations;
    }

    @Contract("_ -> this")
    public BodyButton setShowDecorations(boolean showDecorations) {
        this.showDecorations = showDecorations;
        return this;
    }

    @Contract(pure = true)
    public boolean isShowTooltip() {
        return this.showTooltip;
    }

    @Contract("_ -> this")
    public BodyButton setShowTooltip(boolean showTooltip) {
        this.showTooltip = showTooltip;
        return this;
    }

    @Contract(pure = true)
    public List<String> getDescriptionMessages() {
        return this.descriptionMessages;
    }

    @Contract("_ -> this")
    public BodyButton setDescriptionMessages(@Nullable List<String> descriptionMessages) {
        this.descriptionMessages = descriptionMessages != null ? descriptionMessages : new ArrayList<>();
        return this;
    }

    @Contract(pure = true)
    public int getDescriptionWidth() {
        return this.descriptionWidth;
    }

    @Contract("_ -> this")
    public BodyButton setDescriptionWidth(int descriptionWidth) {
        this.descriptionWidth = Math.max(1, Math.min(descriptionWidth, 1024));
        return this;
    }

    @Contract(pure = true)
    public int getMessageWidth() {
        return this.messageWidth;
    }

    @Contract("_ -> this")
    public BodyButton setMessageWidth(int messageWidth) {
        this.messageWidth = Math.max(1, Math.min(messageWidth, 1024));
        return this;
    }
}