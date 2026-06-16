package fr.maxlego08.menu.hooks.dialogs.loader.body;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.button.dialogs.BodyButton;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.hooks.dialogs.loader.DialogDynamicAbstractLoader;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.body.DialogDynamicBodyButton;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class DialogDynamicBodyButtonLoader extends DialogDynamicAbstractLoader {

    public DialogDynamicBodyButtonLoader(@NotNull MenuPlugin plugin) {
        super(plugin, "dialog-dynamic-body-button");
    }

    @Override
    protected String getChildPath() {
        return "body.";
    }

    @Override
    protected @Nullable Button wrap(Button button, String buttonName, String start, String end) {
        if (button == null) {
            if (Configuration.enableDebug)
                Logger.info("DialogDynamicBodyButtonLoader: Button is null for buttonName: " + buttonName);
        }
        if (button instanceof BodyButton bodyButton && !button.hasCustomRender()) {
            return new DialogDynamicBodyButton(this.plugin, start, end, bodyButton);
        }
        return null;
    }
}
