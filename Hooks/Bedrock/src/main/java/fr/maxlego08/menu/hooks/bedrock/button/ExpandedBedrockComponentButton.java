package fr.maxlego08.menu.hooks.bedrock.button;

import fr.maxlego08.menu.api.button.buttons.bedrock.components.BedrockComponentButton;
import fr.maxlego08.menu.api.context.BedrockRenderContext;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.geysermc.cumulus.component.ButtonComponent;
import org.jetbrains.annotations.Nullable;

public class ExpandedBedrockComponentButton extends BedrockComponentButton {
    private final BedrockComponentButton original;
    private final int index;

    public ExpandedBedrockComponentButton(BedrockComponentButton original, int index) {
        this.original = original;
        this.index = index;
    }

    @Override
    public void onClick(Player player, InventoryEngine inventoryEngine, int slot, Placeholders placeholders) {
        placeholders.register("index", String.valueOf(this.index));
        this.original.onClick(player, inventoryEngine, slot, placeholders);
    }

    @Override
    public @Nullable ButtonComponent build(BedrockRenderContext<ButtonComponent> context) {
        return this.original.build(context);
    }
}
