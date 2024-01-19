package fr.maxlego08.menu.zcore.utils.attribute;

import org.bukkit.inventory.EquipmentSlot;

public interface IAttribute {
	Attribute.Type getType();

	java.util.UUID getUuid();

	String getName();

	double getAmount();

	EquipmentSlot getSlot();
}
