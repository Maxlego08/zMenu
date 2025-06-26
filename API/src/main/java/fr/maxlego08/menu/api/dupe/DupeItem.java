package fr.maxlego08.menu.api.dupe;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public record DupeItem(ItemStack itemStack, Player player) {

}
