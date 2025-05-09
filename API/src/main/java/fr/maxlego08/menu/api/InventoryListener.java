package fr.maxlego08.menu.api;

import fr.maxlego08.menu.inventory.VInventory;
import fr.maxlego08.menu.zcore.utils.inventory.ItemButton;
import org.bukkit.entity.Player;

public interface InventoryListener {

    default boolean addItem(VInventory inventory, boolean inPlayerInventory, ItemButton itemButton, boolean enableAntiDupe){
        return false;
    }

    default void onInventoryPreOpen(Player player, VInventory inventory, int page, Object... objects){

    }

    default void onInventoryPostOpen(Player player, VInventory inventory){

    }

    default void onInventoryClose(Player player, VInventory inventory){

    }

    default void onButtonClick(Player player, ItemButton button){

    }
}
