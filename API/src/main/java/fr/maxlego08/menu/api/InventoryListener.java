package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import org.bukkit.entity.Player;

public interface InventoryListener {

    default boolean addItem(InventoryEngine inventoryEngine, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe){
        return false;
    }

    default void onInventoryPreOpen(Player player, InventoryEngine inventoryEngine, int page, Object... objects){

    }

    default void onInventoryPostOpen(Player player, InventoryEngine inventoryEngine){

    }

    default void onInventoryClose(Player player, InventoryEngine inventoryEngine){

    }

    default void onButtonClick(Player player, ItemButton button){

    }
}
