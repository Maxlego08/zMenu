package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderInput;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import org.bukkit.entity.Player;

import java.util.Optional;

public class TextInputBuilder implements DialogBuilderInput {
    private final ZDialogManager dialogManager;
    private final MenuPlugin menuPlugin;

    public TextInputBuilder(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.dialogManager = dialogManager;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.TEXT;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        String key = button.getKey();

        int width = button.getWidth();
        String label = this.menuPlugin.parse(player, button.getLabel());
        boolean labelVisible = button.isLabelVisible();
        Optional<String> defaultTextSupplier = button.getDefaultTextSupplier();
        String defaultText;
        if (defaultTextSupplier.isPresent()) {
            defaultText = this.menuPlugin.parse(player, defaultTextSupplier.get());
        } else {
            defaultText = this.menuPlugin.parse(player, button.getDefaultText());
        }
        int maxLength = button.getMaxLength();
        int multilineMaxLines = button.getMultilineMaxLines();
        int multilineHeight = button.getMultilineHeight();
        TextDialogInput.MultilineOptions multilineOptions = null;
        if (multilineMaxLines > 0 && multilineHeight > 0) {
            multilineOptions = TextDialogInput.MultilineOptions.create(multilineMaxLines, multilineHeight);
        }
        if (defaultText.length() > maxLength) {
            if (Configuration.enableInformationMessage) {
                Logger.info("The default text of the text input is longer than the maximum length. Truncating to fit the maximum length.");
            }
            defaultText = defaultText.substring(0, maxLength);
        }
        return DialogInput.text(key,width, this.dialogManager.getPaperComponent().getComponent(label), labelVisible, defaultText, maxLength, multilineOptions);
    }
}
