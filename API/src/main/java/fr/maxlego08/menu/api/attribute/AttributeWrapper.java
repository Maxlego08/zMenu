package fr.maxlego08.menu.api.attribute;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jspecify.annotations.Nullable;

import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

/**
 * Value object for encapsulating all data needed for an attribute modifier on an item: the attribute, operation, amount, and slot.
 * Provides deserialization and conversion to Bukkit AttributeModifier.
 */
public record AttributeWrapper(@NotNull Attribute attribute,@NotNull AttributeModifier.Operation operation, double amount,@NotNull EquipmentSlotGroup slot, @Nullable NamespacedKey namespacedKey) {

    AttributeWrapper(Attribute attribute, AttributeModifier.Operation operation, double amount, EquipmentSlotGroup slot) {
        this(attribute, operation, amount, slot, null);
    }

    public static AttributeWrapper deserialize(@NotNull Map<String, Object> attributeMap) {
        var attribute = Registry.ATTRIBUTE.get(Objects.requireNonNull(NamespacedKey.fromString(((String) attributeMap.get("attribute")).toLowerCase(Locale.ROOT))));
        return new AttributeWrapper(
                attribute,
                AttributeModifier.Operation.valueOf(((String) attributeMap.get("operation")).toUpperCase(Locale.ROOT)),
                ((Number) attributeMap.get("amount")).doubleValue(),
                EquipmentSlotGroup.getByName((String) attributeMap.get("slot"))
        );
    }

    public AttributeModifier toAttributeModifier(MenuPlugin plugin) {
        return new AttributeModifier(Objects.requireNonNullElseGet(this.namespacedKey, () -> new NamespacedKey(plugin, UUID.randomUUID().toString())), this.amount, this.operation, this.slot);
    }
}
