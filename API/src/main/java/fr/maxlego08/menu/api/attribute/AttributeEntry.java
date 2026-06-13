package fr.maxlego08.menu.api.attribute;

import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.jetbrains.annotations.NotNull;

public record AttributeEntry(@NotNull Attribute attribute,@NotNull AttributeModifier modifier) {}