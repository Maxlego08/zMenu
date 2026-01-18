package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public record AttributeWrapper(Attribute attribute, AttributeModifier.Operation operation, double amount, EquipmentSlotGroup slot) {

    public static AttributeWrapper deserialize(@NotNull Map<String, Object> attributeMap) {
        var attribute = Registry.ATTRIBUTE.get(Objects.requireNonNull(NamespacedKey.fromString(((String) attributeMap.get("attribute")).toLowerCase(Locale.ROOT))));
        return new AttributeWrapper(
                attribute,
                AttributeModifier.Operation.valueOf(((String) attributeMap.get("operation")).toUpperCase()),
                ((Number) attributeMap.get("amount")).doubleValue(),
                EquipmentSlotGroup.getByName((String) attributeMap.get("slot"))
        );
    }

    public AttributeModifier toAttributeModifier(MenuPlugin plugin) {
        return new AttributeModifier(new NamespacedKey(plugin, UUID.randomUUID().toString()), amount, operation, slot);
    }
}
