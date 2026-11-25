package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.VanillaDialogInput;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.TextTemplate;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.jspecify.annotations.NonNull;

public class DialogDynamicButton extends InputButton {
    private final String buttonName;
    private final TextTemplate start;
    private final TextTemplate end;

    private final VanillaDialogInput button;

    public DialogDynamicButton(String buttonName, String start, String end, VanillaDialogInput button) {
        this.buttonName = buttonName;
        this.start = TextService.get().flow().compileText(start);
        this.end = TextService.get().flow().compileText(end);
        this.button = button;
    }

    @Override
    public void onRender(@NonNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Placeholders placeholders = context.getPlaceholders();
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.start, context.getPlaceholders(), context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.end, context.getPlaceholders(), context.getPlayer());

        for (int i = startValue; i <= endValue; i++) {
            placeholders.register("index", String.valueOf(i));
            this.button.setKey(this.buttonName + "_" + i);
            DialogInput build = this.button.build(context);
            if (build != null) {
                context.getContent().add(build);
            }
        }
    }

    @Override
    public boolean hasCustomRender() {
        return true;
    }
}
