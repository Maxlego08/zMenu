package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;

public class LabelBuilder implements BedrockBuilderInput {
    private final MenuPlugin menuPlugin;

    public LabelBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.BEDROCK_LABEL;
    }

    @Override
    public Component build(Player player, InputButton button) {
        String text = menuPlugin.parse(player, button.getLabel());

        return LabelComponent.of(text);
    }
}