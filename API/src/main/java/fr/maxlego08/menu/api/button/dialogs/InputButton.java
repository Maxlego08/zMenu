package fr.maxlego08.menu.api.button.dialogs;

import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class InputButton extends DialogButton<DialogInput> {
    protected String key;

    @Contract(pure = true)
    @NotNull
    public String getKey() {
        return this.key;
    }

    @Contract("_ -> this")
    public InputButton setKey(@NotNull String key) {
        this.key = key;
        return this;
    }
}
