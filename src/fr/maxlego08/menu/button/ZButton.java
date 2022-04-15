package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public abstract class ZButton extends ZUtils implements Button {

	private final String buttonName;
	private final ItemStack itemStack;
	private final int slot;
	private final boolean isPermanent;

	/**
	 * @param buttonName
	 * @param itemStack
	 * @param slot
	 * @param isPermanent
	 */
	public ZButton(String buttonName, ItemStack itemStack, int slot, boolean isPermanent) {
		super();
		this.buttonName = buttonName;
		this.itemStack = itemStack;
		this.slot = slot;
		this.isPermanent = isPermanent;
	}

	@Override
	public String getName() {
		return this.buttonName;
	}

	@Override
	public ItemStack getItemStack() {
		return this.itemStack;
	}

	@Override
	public ItemStack getCustomItemStack(Player player) {
		if (this.itemStack == null) {
			return null;
		}
		ItemStack itemStack = this.itemStack.clone();
		return super.playerHead(super.papi(itemStack, player), player);
	}

	@Override
	public int getSlot() {
		return this.slot;
	}

	@Override
	public boolean isClickable() {
		return true;
	}

	@Override
	public boolean isPermament() {
		return this.isPermanent;
	}

	@Override
	public <T extends Button> T toButton(Class<T> classz) {
		return (T) this;
	}

	@Override
	public int getRealSlot(int inventorySize, int page) {
		return this.slot - ((page - 1) * inventorySize);
	}
	
	@Override
	public void onLeftClick(Player player, InventoryClickEvent event, Inventory inventory, int slot, Button button) {
	}
	
	@Override
	public void onRightClick(Player player, InventoryClickEvent event, Inventory inventory, int slot, Button button) {
	}
	
	@Override
	public void onMiddleClick(Player player, InventoryClickEvent event, Inventory inventory, int slot, Button button) {
	}
	
	@Override
	public void onClick(Player player, InventoryClickEvent event, Inventory inventory, int slot, Button button) {
	}

}
