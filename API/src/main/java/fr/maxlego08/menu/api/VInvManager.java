package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.utils.EnumInventory;
import org.bukkit.entity.Player;

public interface VInvManager {
    void createInventory(EnumInventory enumInventory, Player player, int page, Object... objects);

    void createInventory(int id, Player player, int page, Object... objects);
}
