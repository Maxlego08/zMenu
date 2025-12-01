package fr.maxlego08.menu.api.attribute;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;

public record AttributeEntry(Attribute attribute, AttributeModifier modifier) {}