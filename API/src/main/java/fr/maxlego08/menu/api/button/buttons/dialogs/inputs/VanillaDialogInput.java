package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import org.jetbrains.annotations.NotNull;

public abstract class VanillaDialogInput extends InputButton {
    protected final DialogInputType dialogInputType;
    protected final String label;

    protected VanillaDialogInput(@NotNull DialogInputType dialogInputType,@NotNull String label) {
        this.dialogInputType = dialogInputType;
        this.label = label;
    }

    public @NotNull DialogInputType getInputType() {
        return this.dialogInputType;
    }

    public @NotNull String getLabel() {
        return this.label;
    }
}
