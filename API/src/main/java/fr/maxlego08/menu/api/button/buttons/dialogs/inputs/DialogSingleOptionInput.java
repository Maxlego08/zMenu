package fr.maxlego08.menu.api.button.buttons.dialogs.inputs;

import com.google.common.base.Preconditions;
import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.DialogRenderContext;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.inventory.dialog.DialogInventory;
import fr.maxlego08.menu.api.utils.PaperMetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.dialogs.SingleOption;
import fr.robie.minimessageflow.Values;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DialogSingleOptionInput extends VanillaDialogInput {
    private final int width;
    private final boolean labelVisible;
    private final List<SingleOption> singleOptions;

    public DialogSingleOptionInput(@NotNull String label, int width, boolean labelVisible, @NotNull List<@NotNull SingleOption> singleOptions) {
        super(DialogInputType.SINGLE_OPTION, label);
        this.width = Math.clamp(width, 1, 1024);
        this.labelVisible = labelVisible;
        this.singleOptions = singleOptions;
    }

    @Override
    public DialogInput build(@NotNull DialogRenderContext<DialogInput, DialogInventory, PaperMetaUpdater, MenuPlugin> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();

        Values values = placeholders.values();
        Component label = this.getLabelTemplate().render(values, player);
        List<SingleOptionDialogInput.OptionEntry> finalOptions = new ArrayList<>(this.singleOptions.size());
        for (SingleOption option : this.singleOptions) {
            finalOptions.add(SingleOptionDialogInput.OptionEntry.create(
                    option.idTemplate().render(values, player),
                    option.displayTemplate().render(values, player),
                    option.initialValue()));
        }

        return DialogInput.singleOption(Preconditions.checkNotNull(this.key, "Key cannot be null"), this.width, finalOptions, label, this.labelVisible);
    }
}
