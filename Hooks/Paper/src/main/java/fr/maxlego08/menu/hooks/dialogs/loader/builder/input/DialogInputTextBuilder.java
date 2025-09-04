package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.configuration.Config;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.utils.BuilderHelper;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import org.bukkit.entity.Player;

import java.util.Optional;

public class DialogInputTextBuilder extends BuilderHelper implements DialogInputBuilderInt {
    private final ZDialogManager dialogManager;

    public DialogInputTextBuilder(ZDialogManager dialogManager) {
        this.dialogManager = dialogManager;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.TEXT;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();

        int width = button.getWidth();
        String label = papi(button.getLabel(), player);
        boolean labelVisible = button.isLabelVisible();
        Optional<String> defaultTextSupplier = button.getDefaultTextSupplier();
        String defaultText;
        if (defaultTextSupplier.isPresent()) {
            defaultText = papi(defaultTextSupplier.get(), player);
        } else {
            defaultText = papi(button.getDefaultText(), player);
        }
        int maxLength = button.getMaxLength();
        int multilineMaxLines = button.getMultilineMaxLines();
        int multilineHeight = button.getMultilineHeight();
        TextDialogInput.MultilineOptions multilineOptions = null;
        if (multilineMaxLines > 0 && multilineHeight > 0) {
            multilineOptions = TextDialogInput.MultilineOptions.create(multilineMaxLines, multilineHeight);
        }
        if (defaultText.length() > maxLength) {
            if (Config.enableInformationMessage) {
                Logger.info("The default text of the text input is longer than the maximum length. Truncating to fit the maximum length.");
            }
            defaultText = defaultText.substring(0, maxLength);
        }
        return DialogInput.text(key,width, this.dialogManager.getPaperComponent().getComponent(label), labelVisible, defaultText, maxLength, multilineOptions);
    }
}
