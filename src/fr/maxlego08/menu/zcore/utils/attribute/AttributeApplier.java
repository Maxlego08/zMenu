package fr.maxlego08.menu.zcore.utils.attribute;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBTCompoundList;
import fr.maxlego08.menu.api.attribute.IAttribute;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class AttributeApplier {

	List<IAttribute> attributes;
	public AttributeApplier(List<IAttribute> attributes) {
		this.attributes = attributes;
	}

	public void apply(ItemStack itemStack) {
		NBT.modify(itemStack, nbt -> {
			ReadWriteNBTCompoundList attributeModifiers = nbt.getCompoundList("AttributeModifiers");
			for (IAttribute attribute : this.attributes) {
				ReadWriteNBT compound = attributeModifiers.addCompound();
				compound.setString("Name", attribute.getName());
				compound.setUUID("UUID", attribute.getUuid());
				compound.setString("AttributeName", "minecraft:" + attribute.getType().getKey());
				compound.setDouble("Amount", attribute.getAmount());
				if (attribute.getSlot() != null) {
					compound.setString("Slot", attribute.getSlot().name().toLowerCase());
				}
			}
		});
	}
}
