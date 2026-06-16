package fr.maxlego08.menu.api.button.dialogs;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import org.jetbrains.annotations.NotNull;

public abstract class DialogButton<T> extends Button {

    public void onRender(@NotNull DialogRenderContext<T> context) {

    }

}
