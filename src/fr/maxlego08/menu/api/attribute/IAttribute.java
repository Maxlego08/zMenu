package fr.maxlego08.menu.api.attribute;

import org.bukkit.inventory.EquipmentSlot;
import java.util.UUID;

/**
 * Interface defining attributes for a menu item in Bukkit.
 */
public interface IAttribute {

	/**
	 * Gets the type of the attribute.
	 *
	 * @return the type of the attribute.
	 */
	Attribute.Type getType();

	/**
	 * Gets the UUID of the attribute.
	 *
	 * @return the unique UUID of the attribute.
	 */
	UUID getUuid();

	/**
	 * Gets the name of the attribute.
	 *
	 * @return the name of the attribute.
	 */
	String getName();

	/**
	 * Gets the amount affected by this attribute.
	 *
	 * @return the double amount of the attribute.
	 */
	double getAmount();

	/**
	 * Gets the equipment slot associated with this attribute.
	 *
	 * @return the equipment slot ({@link EquipmentSlot}) linked to this attribute.
	 */
	EquipmentSlot getSlot();
}
