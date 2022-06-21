package fr.maxlego08.menu.action.permissible;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import fr.maxlego08.menu.api.action.permissible.ItemPermissible;

public class ZItemPermissible implements ItemPermissible {

	private final Material material;
	private final int amount;

	/**
	 * @param material
	 * @param amount
	 */
	public ZItemPermissible(Material material, int amount) {
		super();
		this.material = material;
		this.amount = amount;
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean hasPermission(Player player) {

		if (this.material == null) {
			return true;
		}

		PlayerInventory inventory = player.getInventory();
		ItemStack itemStack = inventory.getItemInHand();

		if (itemStack == null) {
			return false;
		}

		if (itemStack.getType() != this.material) {
			return false;
		}

		if (this.amount > 0 && itemStack.getAmount() < this.amount) {
			return false;
		}

		return true;
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
