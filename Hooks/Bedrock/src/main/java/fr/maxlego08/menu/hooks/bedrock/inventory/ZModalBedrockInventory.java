package fr.maxlego08.menu.hooks.bedrock.inventory;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.buttons.bedrock.components.BasicBedrockComponentButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.enums.bedrock.BedrockType;
import fr.maxlego08.menu.api.text.TextService;
import fr.maxlego08.menu.api.utils.MetaUpdater;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.form.ModalForm;
import org.geysermc.cumulus.response.ModalFormResponse;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ZModalBedrockInventory extends AbstractBedrockComponentInventory<ModalForm.Builder, ModalForm, ModalFormResponse, BasicBedrockComponentButton> {

    public ZModalBedrockInventory(MenuPlugin plugin, String fileName, String name, String content, List<BasicBedrockComponentButton> bodyButtons) {
        super(plugin, fileName, name, content, bodyButtons, BedrockType.MODAL);
    }

    @Override
    public ModalForm.Builder buildForm(@NotNull Player player, @NotNull MetaUpdater metaUpdater, @NotNull InventoryEngine inventoryEngine) {
        Placeholders placeholders = this.createPlaceholders(player);
        List<BasicBedrockComponentButton> buttons = this.filterByViewRequirement(this.bodyButtons, player, inventoryEngine, placeholders);

        return ModalForm.builder()
                .content(TextService.legacy(this.contentTemplate, placeholders, player))
                .button1(TextService.legacy(buttons.get(0).getTextTemplate(), placeholders, player))
                .button2(TextService.legacy(buttons.get(1).getTextTemplate(), placeholders, player))
                .validResultHandler((form, responseData) -> {
                    placeholders.register("content", form.content());
                    int id = responseData.clickedButtonId();
                    buttons.get(id).onClick(player, inventoryEngine, id, placeholders);
                });
    }
}
