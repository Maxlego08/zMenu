package fr.maxlego08.menu.hooks.bedrock.loader.builder.input;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.dialogs.InputButton;
import fr.maxlego08.menu.api.enums.dialog.DialogInputType;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.loader.builder.BedrockBuilderInput;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.ToggleComponent;
import org.jetbrains.annotations.NotNull;

public class ToggleInputBuilder extends BedrockBuilderInput<MenuPlugin> {

    public ToggleInputBuilder(MenuPlugin menuPlugin) {
        super(menuPlugin, menuPlugin.getMetaUpdater());
    }

    @Override
    public DialogInputType getType() {
        return DialogInputType.BOOLEAN;
    }

    @Override
    public @NotNull Component build(@NotNull Player player, @NotNull InputButton button, @NotNull Placeholders placeholders) {
        String text = this.plugin.parse(player, placeholders.parse(button.getLabel()));
        boolean initialValue = Boolean.parseBoolean(this.plugin.parse(player, placeholders.parse(button.getInitialValueBool())));

        return ToggleComponent.of(this.metaUpdater.getLegacyMessage(text), initialValue);
    }
}
