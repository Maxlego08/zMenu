package fr.maxlego08.menu.api.utils.resolvable.bukkit;

import fr.maxlego08.menu.api.utils.resolvable.ParsableResolvable;
import org.bukkit.inventory.EquipmentSlot;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public final class ResolvableEquipmentSlot extends ParsableResolvable<EquipmentSlot> {

    private ResolvableEquipmentSlot(@Nullable EquipmentSlot resolvedValue, @Nullable String expression) {
        super(resolvedValue, expression);
    }

    public static @NotNull ResolvableEquipmentSlot of(@NotNull EquipmentSlot value) {
        return new ResolvableEquipmentSlot(value, null);
    }

    public static @NotNull ResolvableEquipmentSlot of(@NotNull String expression) {
        return new ResolvableEquipmentSlot(null, expression);
    }

    public static @NotNull ResolvableEquipmentSlot auto(@NotNull String value) {
        if (value.contains("%")) {
            return new ResolvableEquipmentSlot(null, value);
        }
        return auto(value, s -> EquipmentSlot.valueOf(s.toUpperCase(Locale.ROOT)), ResolvableEquipmentSlot::new);
    }

    @Nullable
    @Contract("null -> null; !null -> !null")
    public static ResolvableEquipmentSlot autoOrNull(@Nullable String value) {
        return autoOrNull(value, s -> EquipmentSlot.valueOf(s.toUpperCase(Locale.ROOT)), ResolvableEquipmentSlot::new);
    }

    @Override
    protected @Nullable EquipmentSlot parse(@NotNull String value) {
        try {
            return EquipmentSlot.valueOf(value.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
