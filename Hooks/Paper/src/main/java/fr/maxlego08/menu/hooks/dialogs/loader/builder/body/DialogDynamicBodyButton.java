package fr.maxlego08.menu.hooks.dialogs.loader.builder.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.dialogs.body.VanillaDialogBody;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicAbstractLoader;
import io.papermc.paper.registry.data.dialog.body.DialogBody;
import org.jspecify.annotations.NonNull;

public class DialogDynamicBodyButton extends BodyButton {
    private final MenuPlugin plugin;
    private final String start;
    private final String end;
    private final VanillaDialogBody button;

    public DialogDynamicBodyButton(MenuPlugin plugin, String start, String end, VanillaDialogBody bodyButton) {
        this.plugin = plugin;
        this.start = start;
        this.end = end;
        this.button = bodyButton;
    }

    @Override
    public void onRender(@NonNull DialogRenderContext<DialogBody, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());
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
