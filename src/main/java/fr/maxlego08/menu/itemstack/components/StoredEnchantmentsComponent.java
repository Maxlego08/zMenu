package fr.maxlego08.menu.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public record StoredEnchantmentsComponent(
    @NotNull Map<Enchantment, Integer> storedEnchantments
) implements ItemComponent {
    @Override
    public void apply(@NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, EnchantmentStorageMeta.class, enchantmentStorageMeta -> {
            for (var entry : this.storedEnchantments.entrySet()) {
                enchantmentStorageMeta.addStoredEnchant(entry.getKey(), entry.getValue(), true);
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply StoredEnchantmentsComponent to item: " + itemStack.getType().name());
    }
}
