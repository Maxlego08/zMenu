package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.hooks.bedrock.utils.loader.BedrockInputBuilder;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.ToggleComponent;

public class BedrockToggleInputBuilder implements BedrockInputBuilder {
    private final MenuPlugin menuPlugin;

    public BedrockToggleInputBuilder(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public Component build(Player player, InputButton button) {
        String text = menuPlugin.parse(player, button.getLabel());
        boolean initialValue = Boolean.parseBoolean(menuPlugin.parse(player, button.getInitialValueBool()));

        return ToggleComponent.of(text, initialValue);
    }
}
