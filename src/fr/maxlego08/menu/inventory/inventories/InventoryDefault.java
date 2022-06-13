package fr.maxlego08.menu.inventory.inventories;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;

public class InventoryDefault extends VInventory {

	private Inventory inventory;
	private List<Inventory> oldInventories;
	private List<PlaceholderButton> buttons;
	private int maxPage = 1;

	@Override
	public InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args)
			throws InventoryOpenException {

		this.inventory = (Inventory) args[0];

		InventoryResult result = this.inventory.openInventory(player, this);
		if (result != InventoryResult.SUCCESS) {
			return result;
		}

		this.oldInventories = (List<Inventory>) args[1];

		this.maxPage = this.inventory.getMaxPage(player, args);

		String inventoryName = this.getMessage(this.inventory.getName(), "%page%", page, "%maxPage%", this.maxPage);
		super.createInventory(super.papi(super.color(inventoryName), player), this.inventory.size());

		if (this.inventory.getFillItemStack() != null) {
			for (int a = 0; a != super.getSpigotInventory().getContents().length; a++) {
				this.addItem(a, this.inventory.getFillItemStack());
			}
		}

		this.buttons = this.inventory.sortButtons(page, args);
		this.buttons.forEach(button -> button.onInventoryOpen(player, this));

		this.buttons.forEach(this::buildButton);

		return InventoryResult.SUCCESS;
	}

	@Override
	protected void onClose(InventoryCloseEvent event, MenuPlugin plugin, Player player) {

		this.buttons.forEach(button -> button.onInventoryClose(player, this));

	}

	/**
	 * Allows to display a button
	 * 
	 * @param button
	 */
	private void buildButton(PlaceholderButton button) {

		// If the button has a permission or a placeholder to check
		if (button.hasPermission()) {

			// We will check if the player has the permission to display the
			// button
			if (!button.checkPermission(this.player, this)) {

				// If there is an ElseButton we will display it
				if (button.hasElseButton()) {

					PlaceholderButton elseButton = button.getElseButton().toButton(PlaceholderButton.class);
					this.buildButton(elseButton);

				}

			} else {

				// If the player has the permission, the button
				this.displayButton(button);

			}

		} else {

			// If there is no permission, then the button
			this.displayButton(button);

		}

	}

	/**
	 * Allows to display the button in the inventory
	 * 
	 * @param button
	 */
	private void displayButton(PlaceholderButton button) {

		if (button.hasSpecialRender()) {

			button.onRender(player, this);

		} else {

			this.displayFinalButton(button, button.getRealSlot(this.inventory.size(), this.page));

		}

	}

	/**
	 * Allows to display the button and to put the actions on the clicks
	 * 
	 * @param button
	 * @param slot
	 */
	public void displayFinalButton(PlaceholderButton button, int slot) {

		ItemStack itemStack = button.getCustomItemStack(this.player);
		ItemButton itemButton = this.addItem(slot, itemStack);
		if (button.isClickable()) {
			itemButton.setClick(event -> button.onClick(this.player, event, this, slot));
			itemButton.setLeftClick(event -> button.onLeftClick(this.player, event, this, slot));
			itemButton.setRightClick(event -> button.onRightClick(this.player, event, this, slot));
			itemButton.setMiddleClick(event -> button.onMiddleClick(this.player, event, this, slot));
		}

	}

	/**
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}

	/**
	 * @return the oldInventories
	 */
	public List<Inventory> getOldInventories() {
		return oldInventories;
	}

	/**
	 * @return the maxPage
	 */
	public int getMaxPage() {
		return maxPage;
	}

}
