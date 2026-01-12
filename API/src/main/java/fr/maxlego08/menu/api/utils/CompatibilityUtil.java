package fr.maxlego08.menu.api.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryEvent;
import org.bukkit.inventory.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class CompatibilityUtil {


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
        try {
            Object view = event.getView();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

    @Nullable
    public static Inventory getTopInventory(@NotNull Player player) {
        try {
            Object view = player.getOpenInventory();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            return null;
        }
    }

}
