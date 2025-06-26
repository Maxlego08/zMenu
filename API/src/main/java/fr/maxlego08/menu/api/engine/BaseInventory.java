package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public interface BaseInventory extends InventoryHolder {

    MenuPlugin getPlugin();

    Player getPlayer();

    boolean isClose();

    ItemButton addItem(int slot, ItemStack itemStack);

    ItemButton addItem(int slot, ItemStack itemStack, boolean enableAntiDupe);

    ItemButton addItem(boolean inPlayerInventory, int slot, ItemStack itemStack);

    ItemButton addItem(boolean inPlayerInventory, int slot, ItemStack itemStack, boolean enableAntiDupe);

    String getGuiName();

    Inventory getSpigotInventory();

    Inventory getInventory();

    Object[] getArgs();

    int getPage();

    void removeItem(int slot);

    void removePlayerItem(int slot);

    void clearItem();

    Map<Integer, ItemButton> getItems();

    Map<Integer, ItemButton> getPlayerInventoryItems();

    boolean isDisableClick();

    void setDisableClick(boolean disableClick);

    boolean isDisablePlayerInventoryClick();

    void setDisablePlayerInventoryClick(boolean disablePlayerInventoryClick);
}
