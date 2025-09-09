package fr.maxlego08.menu.hooks.bedrock.utils.loader;

import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;

public interface BedrockInputBuilder {
    DialogInputType getType();
    Component build(Player player, InputButton button);
}