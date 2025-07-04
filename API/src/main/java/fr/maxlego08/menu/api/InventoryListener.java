package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.engine.BaseInventory;
import fr.maxlego08.menu.api.engine.ItemButton;
import org.bukkit.entity.Player;

public interface InventoryListener {

    default boolean addItem(BaseInventory baseInventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe){
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
}
