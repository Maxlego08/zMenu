package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.utils.loader.BedrockInputBuilder;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;

import java.util.Optional;

public class BedrockInputTextBuilder implements BedrockInputBuilder {
    private final MenuPlugin menuPlugin;

    public BedrockInputTextBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.TEXT;
    }

    @Override
    public Component build(Player player, InputButton button) {
        String text = this.menuPlugin.parse(player, button.getLabel());
        String defaultText = this.menuPlugin.parse(player, button.getDefaultText());

        return InputComponent.of(text, defaultText);
    }
}
