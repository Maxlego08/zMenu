package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.InventoryEngine;
import fr.maxlego08.menu.api.engine.ItemButton;
import org.bukkit.entity.Player;

public interface InventoryListener {

    default boolean addItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe){
        return false;
    }

    /**
     * Called when the item of a button already placed is replaced after the inventory has been opened,
     * for example once a player head texture has been loaded.
     *
     * @return true if the listener displayed the item itself and the default placement must be skipped
     */
    default boolean updateItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton) {
        return false;
    }

    default void onInventoryPreOpen(Player player, BaseInventory baseInventory, int page, Object... objects){

    }

    default void onInventoryPostOpen(Player player, BaseInventory baseInventory){

    }

    default void onInventoryClose(Player player, BaseInventory baseInventory){

    }

    default void onButtonClick(Player player, ItemButton button){

    }

    default void onInventorySwitch(Player player, InventoryEngine oldInventoryEngine, InventoryEngine newInventoryEngine) {

    }
}
