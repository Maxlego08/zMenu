package fr.maxlego08.menu.api.animation.slot;

import fr.maxlego08.menu.api.MenuItemStack;

import java.lang.reflect.Proxy;

/**
 * Creates {@link MenuItemStack} stand-ins for the compiler tests. The compiler only ever carries
 * them around, it never builds them, so a proxy that answers nothing is enough.
 */
public final class TestMenuItemStacks {

    private TestMenuItemStacks() {
    }

    public static MenuItemStack named(String name) {
        return (MenuItemStack) Proxy.newProxyInstance(
                TestMenuItemStacks.class.getClassLoader(),
                new Class<?>[]{MenuItemStack.class},
                (proxy, method, args) -> switch (method.getName()) {
                    case "toString" -> name;
                    case "hashCode" -> name.hashCode();
                    case "equals" -> proxy == args[0];
                    default -> null;
                });
    }
}
