package fr.maxlego08.menu.hooks.dialogs.action;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.DialogButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import fr.maxlego08.menu.hooks.ComponentMeta;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.BooleanDialogInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class ZConfigManagerCustomDialogAction implements ZDialogAction {

    public record FieldEntry(
            @NotNull DialogInputType type,
            @NotNull Consumer<Object> consumer
    ) {
    }

    private final Map<String, FieldEntry> fieldEntries;
    private final Consumer<Boolean> updateConsumer;
    private final ComponentMeta componentMeta;
    private final String booleanConfirmText;
    private final String numberRangeConfirmText;
    private final String stringConfirmText;
    private final int usageLimit;
    private final @Nullable TemporalAmount actionDurationLimit;

    public ZConfigManagerCustomDialogAction(
            @NotNull Map<String, FieldEntry> fieldEntries,
            @Nullable Consumer<Boolean> updateConsumer,
            @NotNull ComponentMeta componentMeta,
            @NotNull String booleanConfirmText,
            @NotNull String numberRangeConfirmText,
            @NotNull String stringConfirmText,
            int usageLimit,
            @Nullable TemporalAmount actionDurationLimit
    ) {
        this.fieldEntries = fieldEntries;
        this.updateConsumer = updateConsumer;
        this.componentMeta = componentMeta;
        this.booleanConfirmText = booleanConfirmText;
        this.numberRangeConfirmText = numberRangeConfirmText;
        this.stringConfirmText = stringConfirmText;
        this.usageLimit = usageLimit;
        this.actionDurationLimit = actionDurationLimit;
    }

    @Override
    public @Nullable DialogAction build(
            @NotNull List<@NotNull DialogInput> inputs,
            @NotNull Player player,
            @NotNull MenuPlugin plugin,
            @NotNull InventoryEngine inventoryEngine,
            @Nullable DialogButton<?> button,
            @NotNull Placeholders placeholders
    ) {
        ClickCallback.Options.Builder builder = ClickCallback.Options.builder();
        builder.uses(this.usageLimit);
        if (this.actionDurationLimit != null) {
            builder.lifetime(this.actionDurationLimit);
        }

        return DialogAction.customClick((view, audience) -> {
            StringBuilder sb = new StringBuilder("Config Input Results:\n");
            boolean changed = false;

            for (DialogInput input : inputs) {
                String key = input.key();
                FieldEntry entry = this.fieldEntries.get(key);
                if (entry == null) continue;

                Object value = this.readValue(view, key, entry.type());
                if (value != null) {
                    entry.consumer().accept(value);
                    changed = true;
                }

                String line = this.formatLine(key, value, entry, input);
                if (line != null) {
                    sb.append(line).append("\n");
                }
            }

            if (changed && this.updateConsumer != null) {
                this.updateConsumer.accept(true);
            }

            if (audience instanceof Player p) {
                this.componentMeta.sendMessage(p, sb.toString());
            }
        }, builder.build());
    }

    private Object readValue(io.papermc.paper.dialog.DialogResponseView view, String key, DialogInputType type) {
        return switch (type) {
            case BOOLEAN -> view.getBoolean(key);
            case TEXT, SINGLE_OPTION -> view.getText(key);
            case NUMBER_RANGE -> view.getFloat(key);
        };
    }

    private String formatLine(String key, Object value, FieldEntry entry, DialogInput input) {
        return switch (entry.type()) {
            case BOOLEAN -> {
                if (value instanceof Boolean boolValue && input instanceof BooleanDialogInput booleanInput) {
                    yield this.booleanConfirmText
                            .replace("%key%", key)
                            .replace("%value%", boolValue ? "<green>✔" : "<red>✘")
                            .replace("%text%", boolValue ? booleanInput.onTrue() : booleanInput.onFalse());
                }
                yield null;
            }
            case NUMBER_RANGE -> this.numberRangeConfirmText
                    .replace("%key%", key)
                    .replace("%value%", String.valueOf(value));
            case TEXT, SINGLE_OPTION -> this.stringConfirmText
                    .replace("%key%", key)
                    .replace("%text%", value != null ? value.toString() : "");
        };
    }
}
