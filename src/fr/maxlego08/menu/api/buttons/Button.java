package fr.maxlego08.menu.api.buttons;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Button {

	/**
	 * Returns the name of the button
	 * 
	 * @return name
	 */
	public String getName();
	
	/**
	 * Returns the itemstack that will be displayed
	 * 
	 * @return {@link ItemStack}
	 */
	public ItemStack getItemStack();
	
	/**
	 * This method will return the itemstack that will be used in the inventory with a player in parameter
	 * 
	 * @param player Player who opens the inventory
	 * @return {@link ItemStack}
	 */
	public ItemStack getCustomItemStack(Player player);
	
	/**
	 * Returns the slot used by the button
	 * 
	 * @return slot
	 */
	public int getSlot();
	
	/**
	 * Allows to know if the button can be clicked
	 * 
	 * @return boolean
	 */
	public boolean isClickable();
	
	/**
	 * Allows to know if the button is permanent
	 * A permanent button will always be present in the inventory no matter the page
	 * 
	 * @return boolean
	 */
	public boolean isPermament();
	
	/**
	 * Allows to change the type of a button
	 * 
	 * @param classz
	 * @return T
	 */
	public <T extends Button> T toButton(Class<T> classz);
	
}
