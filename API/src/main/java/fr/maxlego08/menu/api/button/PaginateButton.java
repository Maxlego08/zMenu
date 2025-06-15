package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;

public abstract class PaginateButton extends Button {

    public abstract int getPaginationSize(Player player);

    @Override
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    public boolean isPermanent() {
        return true;
    }
}
