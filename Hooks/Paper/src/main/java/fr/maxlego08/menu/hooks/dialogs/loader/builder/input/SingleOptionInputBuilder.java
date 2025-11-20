package fr.maxlego08.menu.hooks.dialogs.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import fr.maxlego08.menu.hooks.ComponentMeta;
import fr.maxlego08.menu.hooks.dialogs.ZDialogManager;
import fr.maxlego08.menu.hooks.dialogs.loader.builder.DialogBuilderInput;
import io.papermc.paper.registry.data.dialog.input.DialogInput;
import io.papermc.paper.registry.data.dialog.input.SingleOptionDialogInput;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SingleOptionInputBuilder implements DialogBuilderInput {
    private final ZDialogManager dialogManager;
    private final MenuPlugin menuPlugin;

    public SingleOptionInputBuilder(ZDialogManager dialogManager, MenuPlugin menuPlugin) {
        this.dialogManager = dialogManager;
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getBodyType() {
        return DialogInputType.SINGLE_OPTION;
    }

    @Override
    public DialogInput build(Player player, InputButton button) {
        ComponentMeta paperComponent = this.dialogManager.getPaperComponent();
        
        String key = button.getKey();
        int width = button.getWidth();
        Component label = paperComponent.getComponent(this.menuPlugin.parse(player, button.getLabel()));
        boolean labelVisible = button.isLabelVisible();
        List<SingleOption> optionList = button.getSigleOptions();
        List<SingleOptionDialogInput.OptionEntry> finalOptions = new ArrayList<>();
        for (SingleOption option : optionList) {
            finalOptions.add(SingleOptionDialogInput.OptionEntry.create(option.id(),paperComponent.getComponent(option.display()),option.initialValue()));
        }
        
        return DialogInput.singleOption(key, width, finalOptions, label, labelVisible);
    }
}
