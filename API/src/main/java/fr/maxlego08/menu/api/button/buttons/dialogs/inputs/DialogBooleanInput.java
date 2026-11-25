package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.robie.minimessageflow.MiniMessageFlow;
import fr.robie.minimessageflow.TextTemplate;
import fr.robie.minimessageflow.Values;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class DialogBooleanInput extends VanillaDialogInput {
    private final TextTemplate initialValue;
    private final Supplier<Boolean> initialValueSupplier;
    private final TextTemplate onTrueText;
    private final TextTemplate onFalseText;

    public DialogBooleanInput(
            @NotNull String label,
            @NotNull String initialValue,
            @NotNull String onTrueText,
            @NotNull String onFalseText
    ) {
        super(DialogInputType.BOOLEAN, label);
        MiniMessageFlow miniMessageFlow = TextService.get().flow();
        this.initialValue = miniMessageFlow.compileText(initialValue);
        this.initialValueSupplier = null;
        this.onTrueText = miniMessageFlow.compileText(onTrueText);
        this.onFalseText = miniMessageFlow.compileText(onFalseText);
    }

    public Optional<Supplier<Boolean>> getInitialValueSupplierX() {
        return Optional.ofNullable(this.initialValueSupplier);
    }

    @Override
    public DialogInput build(@NotNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Placeholders placeholders = context.getPlaceholders();
        Player player = context.getPlayer();

        String key = this.key;
        Values values = placeholders.values();

        Component label = this.getLabelTemplate().render(values, player);
        boolean initialValue = this.getInitialValueSupplierX().map(Supplier::get).orElseGet(() -> Boolean.parseBoolean(this.initialValue.render(values, player)));

        String onTrueText = this.onTrueText.render(values, player);
        String onFalseText = this.onFalseText.render(values, player);

        return DialogInput.bool(key, label, initialValue, onTrueText, onFalseText);
    }
}
