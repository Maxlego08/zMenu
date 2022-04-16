package fr.maxlego08.menu.button;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public abstract class ZButton extends ZUtils implements Button {

	private String buttonName;
	private ItemStack itemStack;
	private int slot = 0;
	private boolean isPermanent = false;
	private boolean closeInventory = false;

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
		return this.isPermanent ? this.slot : this.slot - ((page - 1) * inventorySize);
	}

	@Override
	public void onLeftClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
	}

	@Override
	public void onRightClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
	}

	@Override
	public void onMiddleClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {
		if (this.closeInventory()) {
			player.closeInventory();
		}
	}

	@Override
	public void onInventoryOpen(Player player, InventoryDefault inventory) {

	}

	@Override
	public boolean closeInventory() {
		return this.closeInventory;
	}

	/**
	 * @param buttonName
	 *            the buttonName to set
	 */
	public void setButtonName(String buttonName) {
		this.buttonName = buttonName;
	}

	/**
	 * @param itemStack
	 *            the itemStack to set
	 */
	public void setItemStack(ItemStack itemStack) {
		this.itemStack = itemStack;
	}

	/**
	 * @param slot
	 *            the slot to set
	 */
	public void setSlot(int slot) {
		this.slot = slot;
	}

	/**
	 * @param isPermanent
	 *            the isPermanent to set
	 */
	public void setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
	}

	/**
	 * 
	 * @param closeInventory
	 */
	public void setCloseInventory(boolean closeInventory) {
		this.closeInventory = closeInventory;
	}

}
