package fr.maxlego08.menu.hooks.dialogs.loader.builder.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.VanillaDialogBody;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicAbstractLoader;
import fr.robie.minimessageflow.TextTemplate;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import org.jspecify.annotations.NonNull;

public class DialogDynamicBodyButton extends BodyButton {
    private final TextTemplate start;
    private final TextTemplate end;
    private final VanillaDialogBody button;

    public DialogDynamicBodyButton(String start, String end, VanillaDialogBody bodyButton) {
        this.start = TextService.get().flow().compileText(start);
        this.end = TextService.get().flow().compileText(end);
        this.button = bodyButton;
    }

    @Override
    public void onRender(@NonNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.start, context.getPlaceholders(), context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.end, context.getPlaceholders(), context.getPlayer());
        Placeholders placeholders = context.getPlaceholders();

        for (int i = startValue; i <= endValue; i++) {
            placeholders.register("index", String.valueOf(i));
            DialogBody build = this.button.build(context);
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
