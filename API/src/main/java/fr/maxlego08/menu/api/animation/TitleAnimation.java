package fr.maxlego08.menu.api.animation;

import fr.maxlego08.menu.api.MenuPlugin;
import org.bukkit.event.inventory.InventoryType;

public interface TitleAnimation {

    PlayerTitleAnimation playTitleAnimation(MenuPlugin plugin, int containerId, InventoryType type, int size, Object... args);
}
