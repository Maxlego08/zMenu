package fr.maxlego08.menu.requirement.permissible;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.requirement.Action;
import fr.maxlego08.menu.api.requirement.permissible.ItemPermissible;
import fr.maxlego08.menu.api.utils.Placeholders;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.requirement.ZPermissible;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class ZItemPermissible extends ZPermissible implements ItemPermissible {

    private final Material material;
    private final int amount;
    private final int modelId;

    public ZItemPermissible(Material material, int amount, List<Action> denyActions, List<Action> successActions, int modelId) {
        super(denyActions, successActions);
        this.material = material;
        this.amount = amount;
        this.modelId = modelId;
    }

    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventoryDefault, Placeholders placeholders) {

        if (this.material == null) {
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        int items = 0;
        ItemStack itemStack = new ItemStack(this.material);
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta != null) {
            if (this.modelId > 0) {
                itemMeta.setCustomModelData(this.modelId);
            }
        }
        itemStack.setItemMeta(itemMeta);

        for (int a = 0; a != 36; a++) {
            ItemStack is = player.getInventory().getContents()[a];
            if (is != null && is.isSimilar(itemStack)) {
                items += is.getAmount();
            }
        }

        return items >= this.amount;
    }

    @Override
    public boolean isValid() {
        return this.material != null;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

}
