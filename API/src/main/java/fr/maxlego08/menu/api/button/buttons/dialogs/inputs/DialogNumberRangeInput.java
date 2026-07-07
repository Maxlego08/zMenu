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
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class DialogNumberRangeInput extends VanillaDialogInput {
    private final int width;
    private final String labelFormat;
    private final float start;
    private final float end;
    private final float step;

    private final String initialValue;
    private final Supplier<Float> initialValueSupplier;

    public DialogNumberRangeInput(
            @NotNull String label,
            int width,
            @NotNull String labelFormat,
            float start,
            float end,
            float step,
            @NotNull String initialValue
    ) {
        super(DialogInputType.NUMBER_RANGE, label);
        this.width = Math.clamp(width, 1, 1024);
        this.labelFormat = labelFormat;
        this.start = start;
        this.end = end;
        this.step = step;
        this.initialValue = initialValue;
        this.initialValueSupplier = null;
    }

    public Optional<Supplier<Float>> getInitialValueSupplierX() {
        return Optional.ofNullable(this.initialValueSupplier);
    }

    @Override
    public DialogInput build(@NotNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MenuPlugin plugin = context.getPlugin();

        Component label = context.getMetaUpdater().getComponent(plugin.parse(player, placeholders.parse(this.label)));
        String labelFormat = plugin.parse(player, placeholders.parse(this.labelFormat));

        float start = this.start;
        float end = this.end;
        float step = this.step;

        Optional<Supplier<Float>> initialValueSupplierX = this.getInitialValueSupplierX();
        float initialValue = initialValueSupplierX.map(Supplier::get).orElseGet(() -> {
            try {
                return Float.parseFloat(plugin.parse(player, placeholders.parse(this.initialValue)));
            } catch (NumberFormatException e) {
                return (start + end) / 2;
            }
        });

        if (initialValue > end || initialValue < start) {
            if (Configuration.enableDebug) {
                Logger.info("The initial value of the number range input is out of bounds. Start: " + start + ", End: " + end + ", Initial Value: " + initialValue + ". Setting to middle value.");
            }
            initialValue = (start + end) / 2;
        }
        return DialogInput.numberRange(this.key, this.width, label, labelFormat, start, end, initialValue, step);
    }
}