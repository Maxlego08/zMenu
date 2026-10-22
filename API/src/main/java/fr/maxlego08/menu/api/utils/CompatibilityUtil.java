package fr.maxlego08.menu.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Utility helper for resolving plugin compatibility issues between Bukkit versions using reflection.
 */
public class CompatibilityUtil {

    /**
     * getTopInventory is called on every move, click and packet, so the lookup is resolved once per view class.
     * Holds null when the class has no such method.
     */
    private static final ClassValue<Method> TOP_INVENTORY_METHODS = new ClassValue<>() {
        @Override
        protected Method computeValue(@NotNull Class<?> type) {
            try {
                Method method = type.getMethod("getTopInventory");
                method.setAccessible(true);
                return method;
            } catch (NoSuchMethodException | RuntimeException e) {
                return null;
            }
        }
    };

    /**
     * In API versions 1.20.6 and earlier, InventoryView is a class.
     * In versions 1.21 and later, it is an interface.
     * This method uses reflection to get the top Inventory object from the
     * InventoryView associated with an InventoryEvent, to avoid runtime errors.
     *
     * @param event The generic InventoryEvent with an InventoryView to inspect.
     * @return The top Inventory object from the event's InventoryView.
     */
    @Nullable
    public static Inventory getTopInventory(@NotNull InventoryEvent event) {
        return getTopInventory(event.getView());
    }

    @Nullable
    public static Inventory getTopInventory(@NotNull Player player) {
        return getTopInventory((Object) player.getOpenInventory());
    }

    @Nullable
    private static Inventory getTopInventory(@Nullable Object view) {
        if (view == null) return null;
        Method getTopInventory = TOP_INVENTORY_METHODS.get(view.getClass());
        if (getTopInventory == null) return null;
        try {
            return (Inventory) getTopInventory.invoke(view);
        } catch (InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

}
