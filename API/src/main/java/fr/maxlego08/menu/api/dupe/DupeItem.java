package fr.maxlego08.menu.api.dupe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public record DupeItem(@NotNull ItemStack itemStack,@NotNull Player player) {

}
