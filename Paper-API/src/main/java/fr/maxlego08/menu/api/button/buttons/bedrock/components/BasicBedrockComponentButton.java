package fr.maxlego08.menu.api.button.buttons.bedrock.components;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.NotNull;

public class BasicBedrockComponentButton extends BedrockComponentButton {
    private final String text;
    private final BedrockImageType imageType;
    private final String imageData;

    public BasicBedrockComponentButton(@NotNull String text) {
        this.text = text;
        this.imageType = BedrockImageType.NONE;
        this.imageData = null;
    }

    @Override
    public ButtonComponent build(BedrockRenderContext<ButtonComponent> context) {
        MenuPlugin plugin = context.getPlugin();
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();

        String parsedText = this.parseAndColor(this.text, player, placeholders, plugin);
        if (this.imageType != BedrockImageType.NONE && this.imageData != null) {
            String parsedImageData = this.parseAndColor(this.imageData, player, placeholders, plugin);
            return ButtonComponent.of(parsedText, FormImage.Type.valueOf(this.imageType.toString()), parsedImageData);
        }
        return ButtonComponent.of(parsedText);
    }

    private String parseAndColor(@NotNull String text, @NotNull Player player, @NotNull Placeholders placeholders, @NotNull MenuPlugin plugin) {
        return plugin.getMetaUpdater().getLegacyMessage(plugin.parse(player, placeholders.parse(text)));
    }
}
