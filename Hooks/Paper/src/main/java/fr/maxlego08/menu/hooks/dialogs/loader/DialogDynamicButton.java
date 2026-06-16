package fr.maxlego08.menu.hooks.dialogs.loader;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.dialogs.inputs.VanillaDialogInput;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import org.jspecify.annotations.NonNull;

public class DialogDynamicButton extends InputButton {
    private final MenuPlugin plugin;
    private final String buttonName;
    private final String start;
    private final String end;

    private final VanillaDialogInput button;

    public DialogDynamicButton(MenuPlugin plugin, String buttonName, String start, String end, VanillaDialogInput button) {
        this.plugin = plugin;
        this.buttonName = buttonName;
        this.start = start;
        this.end = end;
        this.button = button;
    }

    @Override
    public void onRender(@NonNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Placeholders placeholders = context.getPlaceholders();
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());

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
