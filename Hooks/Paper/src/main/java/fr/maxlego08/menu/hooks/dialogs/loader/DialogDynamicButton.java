package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderClass;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;

import java.util.Optional;

public class DialogDynamicButton extends InputButton {
    private final MenuPlugin plugin;
    private final String buttonName;
    private final String start;
    private final String end;

    private final InputButton button;

    public DialogDynamicButton(MenuPlugin plugin, String buttonName, String start, String end, InputButton button) {
        super(null);
        this.plugin = plugin;
        this.buttonName = buttonName;
        this.start = start;
        this.end = end;
        this.button = button;
    }

    @Override
    public void onRender(DialogRenderContext<DialogInput> context) {
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());

        for (int i = startValue; i <= endValue; i++) {
            DialogInputType inputType = this.button.getInputType();
            if (inputType != null) {
                Optional<DialogBuilderInput> dialogInputBuilder = DialogBuilderClass.getDialogInputBuilder(inputType);
                if (dialogInputBuilder.isPresent()) {
                    DialogBuilderInput builder = dialogInputBuilder.get();
                    Placeholders placeholders = new Placeholders();
                    placeholders.register("index", String.valueOf(i));
                    this.button.setKey(this.buttonName + "_" + i);
                    DialogInput build = builder.build(context.getPlayer(), this.button, placeholders);
                    if (build != null) {
                        context.getContent().add(build);
                    }
                }
            }
        }
    }

    @Override
    public boolean hasCustomRender() {
        return true;
    }
}
