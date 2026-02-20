package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;

import java.util.ArrayList;
import java.util.List;

public class DropdownInputBuilder implements BedrockBuilderInput {
    private final MenuPlugin menuPlugin;

    public DropdownInputBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.SINGLE_OPTION;
    }

    @Override
    public Component build(Player player, InputButton button) {
        String text = this.menuPlugin.parse(player, button.getLabel());
        List<SingleOption> optionList = button.getSigleOptions();

        List<String> options = new ArrayList<>();
        int defaultOption = 0;
        if (optionList != null) {
            for (int i = 0; i < optionList.size(); i++) {
                SingleOption option = optionList.get(i);
                options.add(option.display());
                if (option.initialValue()) {
                    defaultOption = i;
                }
            }
        }

        return DropdownComponent.of(text, options, defaultOption);
    }
}
