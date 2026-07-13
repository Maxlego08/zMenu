package fr.maxlego08.menu.hooks.dialogs.action;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.DialogButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.ZDialogAction;
import io.papermc.paper.registry.data.dialog.action.DialogAction;
import io.papermc.paper.registry.data.dialog.input.*;
import net.kyori.adventure.text.event.ClickCallback;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.time.temporal.TemporalAmount;
import java.util.List;
import java.util.Map;

public class ZCustomClickDialogAction implements ZDialogAction {
    private final @NotNull List<@NotNull Requirement> requirements;
    private final int usageLimit;
    private final @Nullable TemporalAmount actionDurationLimit;
    private final Map<String, Boolean> enablePlaceholders;

    public ZCustomClickDialogAction(@NotNull List<@NotNull Requirement> requirements, int usageLimit, @Nullable TemporalAmount actionDurationLimit, Map<String, Boolean> enablePlaceholders) {
        this.requirements = requirements;
        this.usageLimit = usageLimit;
        this.actionDurationLimit = actionDurationLimit;
        this.enablePlaceholders = enablePlaceholders;
    }

    @Override
    public DialogAction build(@NotNull List<@NotNull DialogInput> inputs, @NotNull Player player, @NotNull MenuPlugin plugin, @NotNull InventoryEngine inventoryEngine, @Nullable DialogButton<?> button, @NotNull Placeholders placeholders) {
        ClickCallback.Options.Builder builder = ClickCallback.Options.builder();
        builder.uses(this.usageLimit);
        if (this.actionDurationLimit != null) {
            builder.lifetime(this.actionDurationLimit);
        }
        return DialogAction.customClick((view, audience)-> {
            for (DialogInput input : inputs) {
                String key = input.key();
                String value = null;

                String typeKey;
                Object rawValue;

                switch (input) {
                    case NumberRangeDialogInput numberRangeDialogInput -> {
                        rawValue = view.getFloat(key);
                        value = String.valueOf(rawValue);
                        typeKey = "number-range";
                    }
                    case TextDialogInput textDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                        typeKey = "text";
                    }
                    case BooleanDialogInput booleanDialogInput -> {
                        rawValue = view.getBoolean(key);
                        value = String.valueOf(rawValue);
                        placeholders.register(key+"_text", (Boolean) rawValue ? booleanDialogInput.onTrue() : booleanDialogInput.onFalse());
                        typeKey = "boolean";
                    }
                    case SingleOptionDialogInput singleOptionDialogInput -> {
                        rawValue = view.getText(key);
                        value = (String) rawValue;
                        typeKey = "single-option";
                    }
                    default -> {
                        typeKey = null;
                    }
                }
                if (value == null) {
                    continue;
                }

                if (!this.enablePlaceholders.getOrDefault(typeKey, this.enablePlaceholders.getOrDefault("", false))) {
                    value = value.replace("%", "\uF000");
                }

                placeholders.register(key, value);
            }

            for (Requirement requirement : this.requirements) {
                requirement.execute((Player) audience, button, inventoryEngine, placeholders);
            }

        }, builder.build());
    }
}
