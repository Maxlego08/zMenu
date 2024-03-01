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

import java.util.List;

public class ZItemPermissible extends ZPermissible implements ItemPermissible {

    private final Material material;
    private final int amount;

    /**
     * @param material
     * @param amount
     */
    public ZItemPermissible(Material material, int amount, List<Action> denyActions, List<Action> successActions) {
        super(denyActions, successActions);
        this.material = material;
        this.amount = amount;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasPermission(Player player, Button button, InventoryDefault inventoryDefault, Placeholders placeholders) {

        if (this.material == null) {
            return true;
        }

        PlayerInventory inventory = player.getInventory();
        ItemStack itemStack = inventory.getItemInHand();

        if (itemStack == null || itemStack.getType() != this.material) {
            return false;
        }

		return this.amount <= 0 || itemStack.getAmount() >= this.amount;
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
