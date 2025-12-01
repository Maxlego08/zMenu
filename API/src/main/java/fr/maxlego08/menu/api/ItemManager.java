package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.mechanic.MechanicFactory;
import fr.maxlego08.menu.api.mechanic.MechanicListener;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.Optional;
import java.util.Set;

public interface ItemManager {

    void loadAll();

    void loadCustomItems();

    void loadCustomItem(File file);

    void reloadCustomItems();

    boolean isCustomItem(String itemId);

    boolean isCustomItem(ItemStack itemStack);

    Optional<String> getItemId(ItemStack itemStack);

    Set<String> getItemIds();

    void registerListeners(Plugin plugin, String mechanicId, MechanicListener listener);

    void unloadListeners();

    void registerMechanicFactory(MechanicFactory factory);

    void giveItem(Player player, String itemId);

    void executeCheckInventoryItems(Player player);
}
