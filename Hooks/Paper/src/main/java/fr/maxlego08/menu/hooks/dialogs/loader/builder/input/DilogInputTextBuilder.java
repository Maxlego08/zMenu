package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.hooks.PaperComponent;
import fr.maxlego08.menu.hooks.dialogs.buttons.InputButton;
import fr.maxlego08.menu.api.enums.DialogInputType;
import fr.maxlego08.menu.hooks.dialogs.utils.loader.DialogInputBuilderInt;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;

public class DilogInputTextBuilder implements DialogInputBuilderInt {

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.TEXT;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        PaperComponent paperComponent = PaperComponent.getInstance();
        String key = button.getKey();

        int width = button.getWidth();
        String label = PlaceholderAPI.setPlaceholders(player, button.getLabel());
        boolean labelVisible = button.isLabelVisible();
        String defaultText = PlaceholderAPI.setPlaceholders(player, button.getDefaultText());
        int maxLength = button.getMaxLength();
        int multilineMaxLines = button.getMultilineMaxLines();
        int multilineHeight = button.getMultilineHeight();
        TextDialogInput.MultilineOptions multilineOptions = null;
        if (multilineMaxLines > 0 && multilineHeight > 0) {
            multilineOptions = TextDialogInput.MultilineOptions.create(multilineMaxLines, multilineHeight);
        }

        return DialogInput.text(key,width, paperComponent.getComponent(label), labelVisible, defaultText, maxLength, multilineOptions);
    }
}
