package fr.maxlego08.menu.button;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import fr.maxlego08.menu.MenuItemStack;
import fr.maxlego08.menu.api.button.Button;
import fr.maxlego08.menu.api.sound.SoundOption;
import fr.maxlego08.menu.api.utils.OpenLink;
import fr.maxlego08.menu.inventory.inventories.InventoryDefault;
import fr.maxlego08.menu.zcore.utils.PlayerSkin;
import fr.maxlego08.menu.zcore.utils.ZOpenLink;
import fr.maxlego08.menu.zcore.utils.ZUtils;

public abstract class ZButton extends ZUtils implements Button {

	private String buttonName;
	private MenuItemStack itemStack;
	private int slot = 0;
	private boolean isPermanent = false;
	private boolean closeInventory = false;
	private List<String> messages = new ArrayList<String>();
	private SoundOption soundOption;
	private String playerHead;
	private OpenLink openLink = new ZOpenLink();
	private boolean isUpdated = false;
	private boolean refreshOnClick = false;

	@Override
	public String getName() {
		return this.buttonName;
	}

	@Override
	public MenuItemStack getItemStack() {
		return this.itemStack;
	}

	@SuppressWarnings("deprecation")
	@Override
	public ItemStack getCustomItemStack(Player player) {
		if (this.itemStack == null) {
			return null;
		}
		ItemStack itemStack = this.itemStack.build(player);

		if (this.playerHead != null && itemStack.getItemMeta() instanceof SkullMeta) {

			String name = papi(this.playerHead.replace("%player%", player.getName()), player);
			String texture = PlayerSkin.getTexture(name);
			if (texture == null) {

				SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
				skullMeta.setOwner(name);
				itemStack.setItemMeta(skullMeta);

			} else {

				this.applyTexture(itemStack, texture);

			}

		}

		return super.papi(itemStack, player);
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
	public List<String> getMessages() {
		return this.messages;
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
	public SoundOption getSound() {
		return this.soundOption;
	}

	@Override
	public boolean hasSpecialRender() {
		return false;
	}

	@Override
	public String getPlayerHead() {
		return this.playerHead;
	}

	@Override
	public void onRender(Player player, InventoryDefault inventory) {
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
	public void onInventoryClose(Player player, InventoryDefault inventory) {

	}

	@Override
	public void onClick(Player player, InventoryClickEvent event, InventoryDefault inventory, int slot) {

		if (this.closeInventory()) {
			player.closeInventory();
		}

		if (this.messages.size() > 0) {

			if (this.openLink != null) {

				if (this.openLink != null) {

					this.openLink.send(player, this.messages);

				} else {

					this.messages.forEach(message -> player.sendMessage(this.papi(message, player)));

				}

			} else {

				this.messages.forEach(message -> player.sendMessage(this.papi(message, player)));

			}
		}

		if (this.soundOption != null) {
			this.soundOption.play(player);
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
	public ZButton setButtonName(String buttonName) {
		this.buttonName = buttonName;
		return this;
	}

	/**
	 * @param itemStack
	 *            the itemStack to set
	 */
	public ZButton setItemStack(MenuItemStack itemStack) {
		this.itemStack = itemStack;
		return this;
	}

	/**
	 * @param slot
	 *            the slot to set
	 */
	public ZButton setSlot(int slot) {
		this.slot = slot;
		return this;
	}

	/**
	 * @param isPermanent
	 *            the isPermanent to set
	 */
	public ZButton setPermanent(boolean isPermanent) {
		this.isPermanent = isPermanent;
		return this;
	}

	/**
	 * 
	 * @param closeInventory
	 */
	public ZButton setCloseInventory(boolean closeInventory) {
		this.closeInventory = closeInventory;
		return this;
	}

	/**
	 * 
	 * @param messages
	 */
	public ZButton setMessages(List<String> messages) {
		this.messages = color(messages);
		return this;
	}

	/**
	 * 
	 * @param soundOption
	 */
	public ZButton setSoundOption(SoundOption soundOption) {
		this.soundOption = soundOption;
		return this;
	}

	public ZButton setPlayerHead(String playerHead) {
		this.playerHead = playerHead;
		return this;
	}

	@Override
	public OpenLink getOpenLink() {
		return this.openLink;
	}

	public void setOpenLink(OpenLink openLink) {
		this.openLink = openLink;
	}

	@Override
	public boolean isUpdated() {
		return this.isUpdated;
	}

	public void setUpdated(boolean isUpdated) {
		this.isUpdated = isUpdated;
	}

	@Override
	public boolean isRefreshOnClick() {
		return this.refreshOnClick;
	}

	public void setRefreshOnClick(boolean refreshOnClick) {
		this.refreshOnClick = refreshOnClick;
	}

}
