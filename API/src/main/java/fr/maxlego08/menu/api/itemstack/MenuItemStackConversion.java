package fr.maxlego08.menu.api.itemstack;

import fr.maxlego08.menu.api.MenuItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * The result of turning an item into a menu item.
 *
 * @param menuItemStack         The menu item. When some components could not be converted, it holds the whole
 *                              item as {@code base64:} so that nothing is lost.
 * @param unsupportedComponents The components that forced the {@code base64:} fallback, empty when the menu item
 *                              is readable. A removed component is written {@code !name}.
 */
public record MenuItemStackConversion(@NotNull MenuItemStack menuItemStack, @NotNull List<String> unsupportedComponents) {

    public boolean isReadable() {
        return this.unsupportedComponents.isEmpty();
    }
}
