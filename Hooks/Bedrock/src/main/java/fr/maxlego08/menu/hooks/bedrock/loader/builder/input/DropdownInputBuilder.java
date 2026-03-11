package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.dialogs.record.SingleOption;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class DropdownInputBuilder extends BedrockBuilderInput<MenuPlugin> {

    public DropdownInputBuilder(MenuPlugin menuPlugin) {
        super(menuPlugin, menuPlugin.getMetaUpdater());
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.SINGLE_OPTION;
    }

    @Override
    public @NotNull Component build(@NotNull Player player, @NotNull InputButton button, @NotNull Placeholders placeholders) {
        String text = this.plugin.parse(player, placeholders.parse(button.getLabel()));
        List<SingleOption> optionList = button.getSigleOptions();

        List<String> options = new ArrayList<>();
        int defaultOption = 0;
        if (optionList != null) {
            for (int i = 0; i < optionList.size(); i++) {
                SingleOption option = optionList.get(i);
                options.add(this.plugin.parse(player, placeholders.parse(option.display())));
                if (option.initialValue()) {
                    defaultOption = i;
                }
            }
        }

        return DropdownComponent.of(this.metaUpdater.getLegacyMessage(text), options, defaultOption);
    }
}
