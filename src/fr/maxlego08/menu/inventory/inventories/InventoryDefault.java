package fr.maxlego08.menu.inventory.inventories;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.maxlego08.menu.MenuPlugin;
import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.button.PlaceholderButton;
import fr.maxlego08.menu.api.button.buttons.SlotButton;
import fr.maxlego08.menu.exceptions.InventoryOpenException;
import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.utils.inventory.InventoryResult;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;

public class InventoryDefault extends VInventory {

	private Inventory inventory;
	private List<Inventory> oldInventories;
	private int maxPage = 1;

	@Override
	public InventoryResult openInventory(MenuPlugin main, Player player, int page, Object... args)
			throws InventoryOpenException {

		this.inventory = (Inventory) args[0];
		this.oldInventories = (List<Inventory>) args[1];

		this.maxPage = this.inventory.getMaxPage(args);

		String inventoryName = this.getMessage(this.inventory.getName(), "%page%", page, "%maxPage%", this.maxPage);
		super.createInventory(super.papi(super.color(inventoryName), player), this.inventory.size());

		List<PlaceholderButton> buttons = this.inventory.sortButtons(page, args);
		buttons.forEach(button -> button.onInventoryOpen(player, this));

		buttons.forEach(this::buildButton);

		return InventoryResult.SUCCESS;
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
			if (!button.checkPermission(this.player)) {

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

		if (button instanceof SlotButton) {

			SlotButton slotButton = button.toButton(SlotButton.class);
			slotButton.getSlots().forEach(slot -> {
				this.addItem(slot, button.getCustomItemStack(player)).setClick(event -> event.setCancelled(true));
			});

		} else {

			this.displayFinalButton(button);

		}

	}

	private void displayFinalButton(PlaceholderButton button) {

		int slot = button.getRealSlot(this.inventory.size(), this.page);
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
