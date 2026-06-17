package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.LabelComponent;
import org.jetbrains.annotations.NotNull;

public class BedrockLabel extends VanillaBedrockInput {

    public BedrockLabel(@NotNull String text) {
        super(BedrockComponentType.LABEL, text);
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        return LabelComponent.of(metaUpdater.getLegacyMessage(menuPlugin.parse(player, placeholders.parse(this.text))));
    }
}