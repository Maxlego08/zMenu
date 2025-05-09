package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;

public interface PaginateButton extends Button {

    int getPaginationSize(Player player);

}
