package fr.maxlego08.menu.registry;

import fr.maxlego08.menu.inventory.setter.ContainerInventorySetter;
import fr.maxlego08.menu.loader.container.AnvilInventoryTypeLoader;
import fr.maxlego08.menu.loader.container.ChestInventoryLoader;
import fr.maxlego08.menu.loader.container.ContainerInventoryTypeLoader;
import org.bukkit.event.inventory.InventoryType;

import java.util.EnumMap;
import java.util.Map;
import java.util.Optional;

public class InventoryTypeRegistry {
    private static final InventoryTypeRegistry instance;

    static {
        instance = new InventoryTypeRegistry();
        instance.register(InventoryType.ANVIL, new AnvilInventoryTypeLoader());
        instance.register(InventoryType.CHEST, new ChestInventoryLoader());
    }

    private final Map<InventoryType, ContainerInventoryTypeLoader<? extends ContainerInventorySetter>> loaders = new EnumMap<>(InventoryType.class);

    public static InventoryTypeRegistry getInstance() {
        return instance;
    }

    public void register(InventoryType type, ContainerInventoryTypeLoader<? extends ContainerInventorySetter> loader) {
        this.loaders.put(type, loader);
    }

    public Optional<ContainerInventoryTypeLoader<? extends ContainerInventorySetter>> get(InventoryType type) {
        return Optional.ofNullable(this.loaders.get(type));
    }
}
