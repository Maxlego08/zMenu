package fr.maxlego08.menu.api.attribute;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;

public record ModifierKey(Attribute attribute, AttributeModifier.Operation operation, EquipmentSlotGroup slotGroup) {}