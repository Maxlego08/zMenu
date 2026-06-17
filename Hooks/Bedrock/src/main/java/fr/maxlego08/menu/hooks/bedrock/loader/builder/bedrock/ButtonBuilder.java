package fr.maxlego08.menu.hooks.bedrock.loader.builder.bedrock;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.enums.bedrock.BedrockImageType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.util.FormImage;
import org.jetbrains.annotations.NotNull;

public class ButtonBuilder {
    private final MenuPlugin menuPlugin;
    private final MetaUpdater metaUpdater;

    public ButtonBuilder(@NotNull MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
        this.metaUpdater = this.menuPlugin.getMetaUpdater();
    }

    public ButtonComponent build(Player player, BedrockComponentButton button, Placeholders placeholders) {
        String text = this.parseAndColor(button.getRawText(), player, placeholders);

        BedrockImageType imageType = button.getImageType(player);
        if (imageType == BedrockImageType.NONE){
            return ButtonComponent.of(text);
        }
        return ButtonComponent.of(text, FormImage.Type.valueOf(imageType.toString()), this.parseAndColor(button.getImageData(), player, placeholders));
    }

    private String parseAndColor(@NotNull String text,@NotNull Player player,@NotNull Placeholders placeholders) {
        return this.metaUpdater.getLegacyMessage(this.menuPlugin.parse(player, placeholders.parse(text)));
    }
}