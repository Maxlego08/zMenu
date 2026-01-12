package fr.maxlego08.menu.itemstack;

import fr.maxlego08.menu.api.itemstack.ItemStackSimilar;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jspecify.annotations.NonNull;

import java.util.Objects;

public class LoreSimilar implements ItemStackSimilar {
	@Override
	public @NonNull String getName() {
		return "lore";
	}

	@Override
	public boolean isSimilar(@NonNull ItemStack itemStackA, @NonNull ItemStack itemStackB) {
		if (itemStackA == null || itemStackB == null) return false;
		ItemMeta metaA = itemStackA.getItemMeta();
		ItemMeta metaB = itemStackB.getItemMeta();
		if (metaA == null || metaB == null) return false;
		return metaA.hasLore() && metaB.hasLore() && Objects.equals(metaA.getLore(), metaB.getLore());
	}
}
