package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.text.TextService;
import fr.robie.minimessageflow.Template;
import org.jetbrains.annotations.NotNull;

public abstract class VanillaDialogInput extends InputButton {
    protected final DialogInputType dialogInputType;
    protected final String label;
    protected final Template labelTemplate;

    protected VanillaDialogInput(@NotNull DialogInputType dialogInputType,@NotNull String label) {
        this.dialogInputType = dialogInputType;
        this.label = label;
        this.labelTemplate = TextService.get().flow().compile(label);
    }

    public @NotNull DialogInputType getInputType() {
        return this.dialogInputType;
    }

    public @NotNull Template getLabelTemplate() {
        return this.labelTemplate;
    }

    /**
     * @return the label as written
     * @deprecated render {@link #getLabelTemplate()}; its {@code source()} is the label as written.
     */
    @Deprecated(since = "1.1.2.2")
    public @NotNull String getLabel() {
        return this.label;
    }
}
