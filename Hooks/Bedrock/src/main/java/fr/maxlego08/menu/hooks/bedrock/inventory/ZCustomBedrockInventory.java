package fr.maxlego08.menu.hooks.bedrock.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.inputs.BedrockInputButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.requirement.Requirement;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.hooks.bedrock.AbstractBedrockInventory;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.Component;
import org.geysermc.cumulus.form.CustomForm;
import org.geysermc.cumulus.response.CustomFormResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ZCustomBedrockInventory extends AbstractBedrockInventory<CustomForm.Builder, CustomForm, CustomFormResponse> {
    private final List<BedrockInputButton> bedrockInputButtons;

    public ZCustomBedrockInventory(MenuPlugin plugin, String fileName, String name, List<BedrockInputButton> bedrockInputButtons) {
        super(plugin, fileName, name, BedrockType.CUSTOM);
        this.bedrockInputButtons = bedrockInputButtons;
    }

    @Override
    public CustomForm.Builder buildForm(@NotNull Player player, @NotNull MetaUpdater metaUpdater, @NotNull InventoryEngine inventoryEngine) {
        Placeholders placeholders = this.createPlaceholders(player);
        CustomForm.Builder builder = CustomForm.builder()
                .title(this.getLegacyTitle(player, inventoryEngine, placeholders));

        List<BedrockInputButton> visibleButtons = this.filterByViewRequirement(this.bedrockInputButtons, player, inventoryEngine, placeholders);
        List<BedrockInputButton> expandedButtons = new ArrayList<>();
        this.<Component, BedrockInputButton>renderButtons(visibleButtons, player, placeholders, expandedButtons, (button, context) -> {
            if (button.hasSpecialRender()) {
                button.onRender(context);
            } else {
                Component build = button.build(context);
                if (build != null) {
                    context.getContent().add(build);
                }
            }
        }).forEach(builder::component);

        builder.validResultHandler((form, responseData) -> {
            for (int i = 0; i < expandedButtons.size(); i++) {
                BedrockInputButton input = expandedButtons.get(i);
                Object rawValue = responseData.valueAt(i);
                if (rawValue != null) {
                    placeholders.register(input.getKey(), rawValue.toString());
                }
            }

            for (Requirement requirement : this.getRequirements()) {
                requirement.execute(player, null, inventoryEngine, placeholders);
            }
        });
        return builder;
    }
}
