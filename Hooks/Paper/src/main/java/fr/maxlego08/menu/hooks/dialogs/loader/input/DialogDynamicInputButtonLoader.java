package fr.maxlego08.menu.hooks.dialogs.loader.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.annotations.AutoButtonLoader;
import fr.maxlego08.menu.api.annotations.RequireSupport;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.VanillaDialogInput;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicAbstractLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicButton;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@AutoButtonLoader
@RequireSupport(RequireSupport.SupportType.DIALOG)
public class DialogDynamicInputButtonLoader extends DialogDynamicAbstractLoader {

    public DialogDynamicInputButtonLoader(@NotNull MenuPlugin plugin) {
        super(plugin, "dialog-dynamic-input-button");
    }


    @Override
    protected String getChildPath() {
        return "input.";
    }

    @Override
    protected @Nullable Button wrap(Button button, String buttonName, String start, String end) {
        if (button instanceof VanillaDialogInput inputButton && !button.hasCustomRender()) {
            inputButton.setKey(buttonName);
            return new DialogDynamicButton(this.plugin, buttonName, start, end, inputButton);
        }
        return null;
    }
}
