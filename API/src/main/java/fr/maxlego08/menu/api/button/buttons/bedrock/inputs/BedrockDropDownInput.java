package fr.maxlego08.menu.api.button.buttons.bedrock.inputs;

import fr.robie.minimessageflow.Template;
import fr.maxlego08.menu.api.text.TextService;
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
    /**
     * The options' names compiled once.
     */
    private final transient List<Template> optionTemplates;

    public BedrockDropDownInput(@NotNull String text, @NotNull List<@NotNull BedrockSimpleOption> options) {
        super(BedrockComponentType.DROP_DOWN, text);
        this.options = options;
        List<Template> templates = new ArrayList<>(options.size());
        for (BedrockSimpleOption option : options) {
            templates.add(TextService.get().flow().compile(option.optionName()));
        }
        this.optionTemplates = templates;
    }

    @Override
    public Component build(@NotNull BedrockRenderContext<Component> context) {
        Player player = context.getPlayer();
        Placeholders placeholders = context.getPlaceholders();
        MetaUpdater metaUpdater = context.getMetaUpdater();
        MenuPlugin menuPlugin = context.getPlugin();

        List<String> options = new ArrayList<>();
        int defaultOption = 0;
        for (int i = 0; i < this.options.size(); i++) {
            BedrockSimpleOption option = this.options.get(i);
            options.add(TextService.legacy(this.optionTemplates.get(i), placeholders, player));
            if (option.initialValue()) {
                defaultOption = i;
            }
        }
        return DropdownComponent.of(this.legacyText(context), options, defaultOption);
    }
}
