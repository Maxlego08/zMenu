package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class DialogBooleanInput extends VanillaDialogInput {
    private final String initialValue;
    private final Supplier<Boolean> initialValueSupplier;
    private final String onTrueText;
    private final String onFalseText;

    public DialogBooleanInput(
            @NotNull String label,
            @NotNull String initialValue,
            @NotNull String onTrueText,
            @NotNull String onFalseText
    ) {
        super(DialogInputType.BOOLEAN, label);
        this.initialValue = initialValue;
        this.initialValueSupplier = null;
        this.onTrueText = onTrueText;
        this.onFalseText = onFalseText;
    }

    public Optional<Supplier<Boolean>> getInitialValueSupplierX() {
        return Optional.ofNullable(this.initialValueSupplier);
    }

    @Override
    public DialogInput build(@NotNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Placeholders placeholders = context.getPlaceholders();
        Player player = context.getPlayer();
        MenuPlugin plugin = context.getPlugin();

        String key = this.key;
        Component label = context.getMetaUpdater().getComponent(plugin.parse(player, placeholders.parse(this.getLabel())));
        boolean initialValue = this.getInitialValueSupplierX().map(Supplier::get).orElseGet(() -> Boolean.parseBoolean(plugin.parse(player, placeholders.parse(this.initialValue))));

        String onTrueText = plugin.parse(player, placeholders.parse(this.onTrueText));
        String onFalseText = plugin.parse(player, placeholders.parse(this.onFalseText));

        return DialogInput.bool(key, label, initialValue, onTrueText, onFalseText);
    }
}
