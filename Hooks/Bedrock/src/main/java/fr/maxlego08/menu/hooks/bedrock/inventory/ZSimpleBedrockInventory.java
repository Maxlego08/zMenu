package fr.maxlego08.menu.hooks.bedrock.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.cumulus.response.SimpleFormResponse;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ZSimpleBedrockInventory extends AbstractBedrockComponentInventory<SimpleForm.Builder, SimpleForm, SimpleFormResponse> {

    public ZSimpleBedrockInventory(MenuPlugin plugin, String fileName, String name, String content, List<BedrockComponentButton> bodyButtons) {
        super(plugin, fileName, name, content, bodyButtons, BedrockType.SIMPLE);
    }

    @Override
    public SimpleForm.Builder buildForm(@NotNull Player player, @NotNull MetaUpdater metaUpdater, @NotNull InventoryEngine inventoryEngine) {
        Placeholders placeholders = this.createPlaceholders(player);
        SimpleForm.Builder builder = SimpleForm.builder()
                .title(this.getLegacyTitle(player, inventoryEngine, placeholders))
                .content(this.getLegacyMessage(player, placeholders, this.content));

        List<BedrockComponentButton> visibleButtons = this.filterByViewRequirement(this.bodyButtons, player, inventoryEngine, placeholders);
        List<BedrockComponentButton> expandedButtons = new ArrayList<>();
        this.<ButtonComponent, BedrockComponentButton>renderButtons(visibleButtons, player, placeholders, expandedButtons, (button, context) -> {
            if (button.hasSpecialRender()) {
                button.onRender(context);
            } else {
                ButtonComponent build = button.build(context);
                if (build != null) {
                    context.getContent().add(build);
                }
            }
        }).forEach(builder::button);

        builder.validResultHandler((form, responseData) -> {
            placeholders.register("content", form.content());
            int slot = responseData.clickedButtonId();
            expandedButtons.get(slot).onClick(player, inventoryEngine, slot, placeholders);
        });
        return builder;
    }
}
