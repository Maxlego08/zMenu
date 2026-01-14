package fr.maxlego08.menu.api.itemstack;

import org.bukkit.OfflinePlayer;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemComponent {

    void apply(@NotNull ItemStack itemStack, @Nullable OfflinePlayer player);

}
