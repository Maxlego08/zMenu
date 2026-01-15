package fr.maxlego08.menu.api.itemstack;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemComponent {

    void apply(@NotNull org.bukkit.inventory.ItemStack itemStack, @Nullable Player player);

}
