package fr.maxlego08.menu.hooks.bedrock.loader.builder;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;

import java.util.ArrayList;
import java.util.List;

public abstract class BedrockBuilderManager {
    protected final MenuPlugin menuPlugin;

    public BedrockBuilderManager(MenuPlugin menuPlugin) {
        this.menuPlugin = menuPlugin;
    }

    protected List<Component> getInputComponents(Player player, List<BedrockInputButton> inputButtons, Placeholders placeholders) {
        return this.buildComponents(
                inputButtons,
                player
        );
    }

    protected <B extends BedrockInputButton> List<Component> buildComponents(
            List<B> buttons,
            Player player
    ) {
        List<Component> result = new ArrayList<>();
        //TODO: remodifier
        BedrockRenderContext bedrockRenderContext = new BedrockRenderContext(result, player, null, this.menuPlugin.getMetaUpdater(), new Placeholders(), this.menuPlugin);
        for (B button : buttons) {
            if (button.hasSpecialRender()) {
                button.onRender(bedrockRenderContext);
            } else {
                Component build = button.build(bedrockRenderContext);
                if (build != null) {
                    result.add(build);
                }
            }
        }
        return result;
    }
}