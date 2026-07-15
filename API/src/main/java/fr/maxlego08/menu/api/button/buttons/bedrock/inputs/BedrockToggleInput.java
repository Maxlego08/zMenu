package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.ToggleComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockToggleInput extends VanillaBedrockInput {
    private final String defaultValue;

    public BedrockToggleInput(@NotNull String text, @NotNull String defaultValue) {
        super(BedrockComponentType.TOGGLE, text);
        this.defaultValue = defaultValue;
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        String text = menuPlugin.parse(player, placeholders.parse(this.text));
        boolean defaultValue = Boolean.parseBoolean(menuPlugin.parse(player, placeholders.parse(this.defaultValue)));
        return ToggleComponent.of(metaUpdater.getLegacyMessage(text), defaultValue);
    }
}