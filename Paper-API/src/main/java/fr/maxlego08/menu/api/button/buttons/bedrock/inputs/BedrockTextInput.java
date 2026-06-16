package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.InputComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockTextInput extends VanillaBedrockInput {
    private final String defaultText;

    public BedrockTextInput(@NotNull String text, @NotNull String defaultText) {
        super(BedrockComponentType.INPUT, text);
        this.defaultText = defaultText;
    }

    @Override
    public Component build(@NotNull BedrockRenderContext context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        String text = menuPlugin.parse(player, placeholders.parse(this.text));
        String defaultText = menuPlugin.parse(player, placeholders.parse(this.defaultText));
        return InputComponent.of(metaUpdater.getLegacyMessage(text), defaultText);
    }
}
