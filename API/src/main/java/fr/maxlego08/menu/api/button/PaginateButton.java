package fr.maxlego08.menu.api.button;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public abstract class PaginateButton extends Button {

    public abstract int getPaginationSize(@NotNull Player player);

    @Override
    @Contract(pure = true)
    public boolean hasSpecialRender() {
        return true;
    }

    @Override
    @Contract(pure = true)
    public boolean isPermanent() {
        return true;
    }
}
