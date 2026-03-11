package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.jetbrains.annotations.NotNull;

public class InputTextBuilder extends BedrockBuilderInput<MenuPlugin> {
    public InputTextBuilder(MenuPlugin menuPlugin) {
        super(menuPlugin, menuPlugin.getMetaUpdater());
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.TEXT;
    }

    @Override
    public @NotNull Component build(@NotNull Player player, @NotNull InputButton button, @NotNull Placeholders placeholders) {
        String text = this.plugin.parse(player, placeholders.parse(button.getLabel()));
        String defaultText = this.plugin.parse(player, placeholders.parse(button.getDefaultText()));

        return InputComponent.of(this.metaUpdater.getLegacyMessage(text), defaultText);
    }
}
