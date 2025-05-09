package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface MenuItemStack {

    ItemStack build(Player player);

    ItemStack build(Player player, boolean useCache);

    ItemStack build(Player player, boolean useCache, Placeholders placeholders);

}
