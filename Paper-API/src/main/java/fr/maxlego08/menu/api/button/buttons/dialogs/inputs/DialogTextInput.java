package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.zcore.logger.Logger;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.TextDialogInput;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class DialogTextInput extends VanillaDialogInput {
    private final int width;
    private final boolean labelVisible;
    private final Supplier<String> defaultTextSupplier;
    private final String defaultText;
    private final Integer maxLength;
    private final Integer multilineMaxLines;
    private final Integer multilineHeight; // Value between 1 and 512 — Height of input.

    public DialogTextInput(
            String label,
            Boolean textVisible,
            String defaultValue,
            int width,
            Integer maxLength,
            Integer multilineMaxLines,
            Integer multilineHeight
    ) {
        super(DialogInputType.TEXT, label);
        this.width = Math.clamp(width, 1, 1024);
        this.labelVisible = textVisible;
        this.defaultTextSupplier = null;
        this.defaultText = defaultValue;
        this.maxLength = maxLength;
        this.multilineMaxLines = multilineMaxLines != null ? Math.max(multilineMaxLines, 1) : null;
        this.multilineHeight = multilineHeight != null ? Math.clamp(multilineHeight, 1, 512) : null;
    }

    public Optional<Supplier<String>> getDefaultTextSupplierX() {
        return Optional.ofNullable(this.defaultTextSupplier);
    }

    @Override
    public DialogInput build(@NotNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MenuPlugin plugin = context.getPlugin();

        String label = plugin.parse(player, placeholders.parse(this.label));
        Optional<Supplier<String>> defaultTextSupplierX = this.getDefaultTextSupplierX();
        String defaultText = plugin.parse(player, placeholders.parse(defaultTextSupplierX.map(Supplier::get).orElse(this.defaultText)));

        int maxLength = this.maxLength != null ? this.maxLength : defaultText.length();
        int multilineMaxLines = this.multilineMaxLines;
        int multilineHeight = this.multilineHeight;

        TextDialogInput.MultilineOptions multilineOptions = null;
        if (multilineMaxLines > 0 && multilineHeight > 0) {
            multilineOptions = TextDialogInput.MultilineOptions.create(multilineMaxLines, multilineHeight);
        }

        if (defaultText.length() > maxLength) {
            if (Configuration.enableInformationMessage) {
                Logger.info("The default text of the text input is longer than the maximum length. Truncating to fit the maximum length.", Logger.LogType.WARNING);
            }
            defaultText = defaultText.substring(0, maxLength);
        }
        return DialogInput.text(this.key, this.width, context.getMetaUpdater().getComponent(label), this.labelVisible, defaultText, maxLength, multilineOptions);
    }
}
