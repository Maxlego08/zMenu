package fr.maxlego08.menu.api.engine;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.entity.Player;

public interface BaseInventory {

    MenuPlugin getPlugin();

    Player getPlayer();

}
