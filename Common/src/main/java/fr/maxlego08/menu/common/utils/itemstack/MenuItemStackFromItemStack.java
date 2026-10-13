package fr.maxlego08.menu.common.utils.itemstack;

import fr.maxlego08.menu.ZMenuItemStack;
import fr.maxlego08.menu.api.ComponentsManager;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.itemstack.MenuItemStackConversion;
import fr.maxlego08.menu.api.loader.ItemComponentLoader;
import fr.maxlego08.menu.api.utils.version.MinecraftVersion;
import fr.maxlego08.menu.common.utils.nms.ItemStackUtils;
import io.papermc.paper.datacomponent.DataComponentType;
import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MenuItemStackFromItemStack {

    private static final String CUSTOM_DATA = "custom-data";

    public static ZMenuItemStack fromItemStack(InventoryManager manager, ItemStack itemStack) {
        return (ZMenuItemStack) convert(manager, itemStack).menuItemStack();
    }

    /**
     * Every setting the item has becomes the matching zMenu component. The result is then built and compared to
     * the item: if anything differs, or a setting cannot be converted, the whole item is kept as {@code base64:}.
     */
    public static @NotNull MenuItemStackConversion convert(@NotNull InventoryManager manager, @NotNull ItemStack itemStack) {
        return convert(manager, itemStack, MinecraftVersion.isServerAtLeast("1.21.3"));
    }

    /**
     * @param dataComponents true to read Paper's data components, false to read {@link ItemMeta} as servers
     *                       before 1.21.3 do.
     */
    public static @NotNull MenuItemStackConversion convert(@NotNull InventoryManager manager, @NotNull ItemStack itemStack, boolean dataComponents) {
        ZMenuItemStack menuItemStack = createMenuItemStack(manager, itemStack);
        ComponentsManager componentsManager = manager.getPlugin().getComponentsManager();

        List<String> unsupported = dataComponents
                ? addDataComponents(componentsManager, itemStack, menuItemStack)
                : addItemMetaComponents(componentsManager, itemStack, menuItemStack);

        if (unsupported.isEmpty()) {
            unsupported = differences(itemStack, menuItemStack, dataComponents);
        }

        if (!unsupported.isEmpty()) {
            return new MenuItemStackConversion(fromBase64(manager, itemStack), unsupported);
        }
        return new MenuItemStackConversion(menuItemStack, List.of());
    }

    private static List<String> addDataComponents(ComponentsManager componentsManager, ItemStack itemStack, ZMenuItemStack menuItemStack) {
        List<String> unsupported = new ArrayList<>();

        for (DataComponentType type : RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE)) {
            if (!itemStack.isDataOverridden(type)) continue;

            String name = componentName(type);
            if (!itemStack.hasData(type)) {
                unsupported.add("!" + name);
                continue;
            }

            Optional<ItemComponentLoader> loader = componentsManager.getLoader(name);
            ItemComponent component = loader.map(componentLoader -> componentLoader.fromItemStack(itemStack)).orElse(null);
            if (component == null) {
                unsupported.add(name);
                continue;
            }
            component.setParentLoader(loader.get());
            menuItemStack.addItemComponent(component);
        }

        boolean hasCustomData = menuItemStack.getItemComponents().stream().anyMatch(component -> component.getParentLoader().getComponentName().equals(CUSTOM_DATA));
        if (!hasCustomData && !unsupported.contains(CUSTOM_DATA) && !itemStack.getPersistentDataContainer().isEmpty()) {
            Optional<ItemComponentLoader> loader = componentsManager.getLoader(CUSTOM_DATA);
            ItemComponent component = loader.map(componentLoader -> componentLoader.fromItemStack(itemStack)).orElse(null);
            if (component == null) {
                unsupported.add(CUSTOM_DATA);
            } else {
                component.setParentLoader(loader.get());
                menuItemStack.addItemComponent(component);
            }
        }
        return unsupported;
    }

    private static List<String> addItemMetaComponents(ComponentsManager componentsManager, ItemStack itemStack, ZMenuItemStack menuItemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return List.of();

        for (ItemComponentLoader loader : componentsManager.getLoaders()) {
            ItemComponent component = loader.fromItemMeta(itemStack);
            if (component == null) continue;
            component.setParentLoader(loader);
            menuItemStack.addItemComponent(component);
        }

        // Settings with no component before data components
        if (!itemMeta.getItemFlags().isEmpty()) menuItemStack.setFlags(new ArrayList<>(itemMeta.getItemFlags()));
        if (itemMeta.isHideTooltip()) menuItemStack.setHideTooltip(true);
        if (itemMeta.isFireResistant()) menuItemStack.setFireResistant(true);
        return List.of();
    }

    /**
     * Builds the menu item and lists what differs from the item: data component names, or {@link ItemMeta} keys.
     */
    private static List<String> differences(ItemStack expected, ZMenuItemStack menuItemStack, boolean dataComponents) {
        ItemStack actual;
        try {
            actual = menuItemStack.build((Player) null, false);
        } catch (RuntimeException | LinkageError exception) {
            // A LinkageError is an API the server does not have: never let it escape a conversion
            return List.of("build: " + exception);
        }
        if (actual == null || actual.getType() != expected.getType()) return List.of("material");
        if (expected.isSimilar(actual)) return List.of();

        List<String> differences = new ArrayList<>();
        if (dataComponents) {
            for (DataComponentType type : RegistryAccess.registryAccess().getRegistry(RegistryKey.DATA_COMPONENT_TYPE)) {
                boolean same = expected.hasData(type) == actual.hasData(type)
                        && (!(type instanceof DataComponentType.Valued<?> valued) || Objects.equals(expected.getData(valued), actual.getData(valued)));
                if (!same) differences.add(componentName(type));
            }
            return differences;
        }

        Map<String, Object> expectedMeta = serializeMeta(expected);
        Map<String, Object> actualMeta = serializeMeta(actual);
        Set<String> keys = new HashSet<>(expectedMeta.keySet());
        keys.addAll(actualMeta.keySet());
        for (String key : keys) {
            if (!Objects.equals(expectedMeta.get(key), actualMeta.get(key))) differences.add(key);
        }
        differences.sort(null);
        return differences;
    }

    private static Map<String, Object> serializeMeta(ItemStack itemStack) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        return itemMeta == null ? Map.of() : itemMeta.serialize();
    }

    private static String componentName(DataComponentType type) {
        return type.key().value().replace('_', '-');
    }

    private static ZMenuItemStack createMenuItemStack(InventoryManager manager, ItemStack itemStack) {
        ZMenuItemStack menuItemStack = new ZMenuItemStack(manager, "", "");
        menuItemStack.setMaterial(itemStack.getType().name());
        if (itemStack.getAmount() > 1) menuItemStack.setAmount(String.valueOf(itemStack.getAmount()));
        return menuItemStack;
    }

    private static ZMenuItemStack fromBase64(InventoryManager manager, ItemStack itemStack) {
        ZMenuItemStack menuItemStack = new ZMenuItemStack(manager, "", "");
        menuItemStack.setMaterial("base64:" + ItemStackUtils.serializeItemStack(itemStack));
        return menuItemStack;
    }
}
