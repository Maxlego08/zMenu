package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.enums.bedrock.BedrockComponentType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.record.bedrock.BedrockSimpleOption;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.component.DropdownComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class BedrockDropDownInput extends VanillaBedrockInput {
    private final List<BedrockSimpleOption> options;

    public BedrockDropDownInput(@NotNull String text, @NotNull List<@NotNull BedrockSimpleOption> options) {
        super(BedrockComponentType.DROP_DOWN, text);
        this.options = options;
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        String text = menuPlugin.parse(player, placeholders.parse(this.text));
        List<String> options = new ArrayList<>();
        int defaultOption = 0;
        for (int i = 0; i < this.options.size(); i++) {
            BedrockSimpleOption option = this.options.get(i);
            options.add(metaUpdater.getLegacyMessage(menuPlugin.parse(player, placeholders.parse(option.optionName()))));
            if (option.initialValue()) {
                defaultOption = i;
            }
        }
        return DropdownComponent.of(metaUpdater.getLegacyMessage(text), options, defaultOption);
    }
}
