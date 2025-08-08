package fr.maxlego08.menu.requirement.actions;

import fr.maxlego08.menu.api.MenuPlugin;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.loader.MaterialLoader;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.api.utils.toast.ToastType;
import org.bukkit.entity.Player;

import java.util.Optional;

public class ToastAction extends Action {

    private final MenuPlugin plugin;
    private final String material;
    private final String message;
    private final ToastType toastType;
    private final String modelId;
    private final boolean glowing;

    public ToastAction(MenuPlugin plugin, String material, String message, ToastType toastType, String modelId, boolean glowing) {
        this.plugin = plugin;
        this.material = material;
        this.message = message;
        this.toastType = toastType;
        this.modelId = modelId;
        this.glowing = glowing;
    }

    @Override
    protected void execute(Player player, Button button, InventoryEngine inventoryEngine, Placeholders placeholders) {

        String finalMaterial = this.plugin.parse(player, placeholders.parse(this.material));
        int finalModelId = Integer.parseInt(this.plugin.parse(player, placeholders.parse(this.modelId)));

        if (finalMaterial.contains(":")) {
            String[] values = finalMaterial.split(":", 2);

            if (values.length == 2) {

                String key = values[0];
                String value = values[1];

                Optional<MaterialLoader> optional = this.plugin.getInventoryManager().getMaterialLoader(key);
                if (optional.isPresent()) {
                    MaterialLoader loader = optional.get();
                    var itemStack = loader.load(player, null, "", value);
                    finalMaterial = itemStack.getType().toString();
                    if (itemStack.hasItemMeta() && itemStack.getItemMeta().hasCustomModelData()) {
                        finalModelId = itemStack.getItemMeta().getCustomModelData();
                    }
                }
            }
        }

        this.plugin.getToastHelper().showToast(finalMaterial, this.plugin.parse(player, placeholders.parse(this.message)), this.toastType, finalModelId, this.glowing, player);
    }
}
