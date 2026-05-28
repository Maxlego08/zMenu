package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.api.registry.Registry;
import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.loader.container.AnvilInventoryTypeLoader;
import fr.maxlego08.menu.loader.container.ChestInventoryLoader;
import fr.maxlego08.menu.loader.container.ContainerInventoryTypeLoader;
import org.bukkit.event.inventory.InventoryType;

public class InventoryTypeRegistry extends Registry<InventoryType, ContainerInventoryTypeLoader<? extends ContainerInventorySetter>> {
    private static final InventoryTypeRegistry instance;

    static {
        instance = new InventoryTypeRegistry();
        instance.register(InventoryType.ANVIL, new AnvilInventoryTypeLoader());
        instance.register(InventoryType.CHEST, new ChestInventoryLoader());
    }

    public static InventoryTypeRegistry getInstance() {
        return instance;
    }
}
