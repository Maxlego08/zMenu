package fr.maxlego08.menu.button.buttons;

import fr.maxlego08.menu.api.Inventory;
import fr.maxlego08.menu.api.InventoryManager;
import fr.maxlego08.menu.api.button.buttons.PreviousButton;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.utils.Placeholders;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.jspecify.annotations.NonNull;
import org.bukkit.configuration.ConfigurationSection;
import fr.maxlego08.menu.api.utils.yaml.YamlSections;

public class ZPreviousButton extends PreviousButton {

    private final InventoryManager inventoryManager;

    public ZPreviousButton(InventoryManager inventoryManager) {
        super();
        this.inventoryManager = inventoryManager;
    }

    @Override
    protected void serializeProperties(@NonNull Section section) {
        section.set("type", "previous");
    }

    @SuppressWarnings("removal")
    @Override
    @Deprecated
    protected void serializeProperties(ConfigurationSection section) {
        YamlSections.write(section, copy -> this.serializeProperties(copy));
    }

    @Override
    public void onClick(@NonNull Player player, @NonNull InventoryClickEvent event, @NonNull InventoryEngine inventory, int slot, @NonNull Placeholders placeholders) {
        super.onClick(player, event, inventory, slot, placeholders);
        if (inventory.getPage() != 1) {
            Inventory toInventory = inventory.getMenuInventory();
            this.inventoryManager.openInventory(player, toInventory, event.isLeftClick() ? inventory.getPage() - 1 : 1, inventory.getOldInventories());
        }
    }

    @Override
    public boolean hasPermission() {
        return true;
    }

    @Override
    public boolean checkPermission(@NonNull Player player, @NonNull InventoryEngine inventory, @NonNull Placeholders placeholders) {
        return inventory.getPage() != 1;
    }
}
