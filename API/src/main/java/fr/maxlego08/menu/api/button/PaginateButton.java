package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;

public abstract class PaginateButton extends Button {

    public abstract int getPaginationSize(Player player);

}
