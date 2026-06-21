package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.utils.resolvable.Resolvable;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableDouble;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableEnum;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableString;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;
import java.util.Map;

public final class ResolvableAttributeWrapper extends Resolvable<AttributeWrapper> {

    private final Resolvable<String> attributeKey;
    private final ResolvableEnum<AttributeModifier.Operation> operation;
    private final Resolvable<Double> amount;
    private final Resolvable<String> slotKey;
    private final @Nullable Resolvable<String> namespacedKey;

    public ResolvableAttributeWrapper(
            @NotNull Resolvable<String> attributeKey,
            @NotNull ResolvableEnum<AttributeModifier.Operation> operation,
            @NotNull Resolvable<Double> amount,
            @NotNull Resolvable<String> slotKey,
            @Nullable Resolvable<String> namespacedKey
    ) {
        this.attributeKey = attributeKey;
        this.operation = operation;
        this.amount = amount;
        this.slotKey = slotKey;
        this.namespacedKey = namespacedKey;
    }

    @Nullable
    public static ResolvableAttributeWrapper fromMap(@NotNull Map<String, Object> map) {
        Object typeObj = map.get("type");
        if (!(typeObj instanceof String typeStr)) return null;

        ResolvableString attributeKey = ResolvableString.auto(typeStr);
        Object opObj = map.get("operation");
        ResolvableEnum<AttributeModifier.Operation> operation;
        if (opObj instanceof String opStr) {
            operation = ResolvableEnum.auto(AttributeModifier.Operation.class, opStr);
        } else {
            operation = ResolvableEnum.of(AttributeModifier.Operation.class, AttributeModifier.Operation.ADD_NUMBER);
        }

        Object amountObj = map.get("amount");
        ResolvableDouble amount;
        if (amountObj instanceof Number number) {
            amount = ResolvableDouble.of(number.doubleValue());
        } else if (amountObj instanceof String strValue) {
            amount = ResolvableDouble.auto(strValue);
        } else {
            amount = ResolvableDouble.of(0.0);
        }

        ResolvableString slotKey = ResolvableString.autoOrNull((String) map.get("slot"));
        if (slotKey == null) slotKey = ResolvableString.of("any");

        Object nameObj = map.get("name");
        ResolvableString namespacedKey = nameObj instanceof String nameStr ? ResolvableString.auto(nameStr) : null;

        return new ResolvableAttributeWrapper(attributeKey, operation, amount, slotKey, namespacedKey);
    }

    @Override
    public @Nullable AttributeWrapper resolve(@NotNull BuildContext context) {
        String attrKeyStr = resolve(context, this.attributeKey);
        AttributeModifier.Operation operation = this.operation.resolve(context);
        Double amountVal = resolve(context, this.amount);
        String slotKeyStr = resolve(context, this.slotKey);
        String nskStr = this.namespacedKey != null ? resolve(context, this.namespacedKey) : null;

        if (attrKeyStr == null || operation == null || amountVal == null || slotKeyStr == null) return null;

        NamespacedKey attrNsk = NamespacedKey.fromString(attrKeyStr.toLowerCase(Locale.ROOT));
        if (attrNsk == null) return null;
        Attribute attribute = Registry.ATTRIBUTE.get(attrNsk);
        if (attribute == null) return null;

        EquipmentSlotGroup slot = EquipmentSlotGroup.getByName(slotKeyStr);
        if (slot == null) return null;

        NamespacedKey nsk = nskStr != null ? NamespacedKey.fromString(nskStr) : null;

        return new AttributeWrapper(attribute, operation, amountVal, slot, nsk);
    }
}
