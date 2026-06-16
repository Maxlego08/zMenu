package fr.maxlego08.menu.hooks.dialogs.loader.builder.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogBodyType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicAbstractLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderBody;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderClass;
import io.papermc.paper.registry.data.dialog.body.DialogBody;

import java.util.Optional;

public class DialogDynamicBodyButton extends BodyButton {
    private final MenuPlugin plugin;
    private final String start;
    private final String end;
    private final BodyButton button;

    public DialogDynamicBodyButton(MenuPlugin plugin, String start, String end, BodyButton bodyButton) {
        super(null);
        this.plugin = plugin;
        this.start = start;
        this.end = end;
        this.button = bodyButton;
    }

    @Override
    public void onRender(DialogRenderContext<DialogBody> context) {
        int startValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.start, context.getPlayer());
        int endValue = DialogDynamicAbstractLoader.stringToInt(this.plugin, this.end, context.getPlayer());

        for (int i = startValue; i <= endValue; i++) {
            DialogBodyType bodyType = this.button.getBodyType();
            if (bodyType != null) {
                Optional<DialogBuilderBody> dialogBuilder = DialogBuilderClass.getDialogBuilder(bodyType);
                if (dialogBuilder.isPresent()) {
                    DialogBuilderBody dialogBuilderBody = dialogBuilder.get();
                    Placeholders placeholders = new Placeholders();
                    placeholders.register("index", String.valueOf(i));
                    DialogBody build = dialogBuilderBody.build(context.getPlayer(), this.button, placeholders);
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
