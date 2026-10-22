package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.StoredEnchantmentsComponent;
import fr.maxlego08.menu.api.utils.ItemUtil;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import fr.maxlego08.menu.zcore.logger.Logger;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.List;

public class LegacyStoredEnchantmentsComponent extends StoredEnchantmentsComponent {

    public LegacyStoredEnchantmentsComponent(@NotNull List<ResolvableEnchantmentEntry> storedEnchantments) {
        super(storedEnchantments);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, EnchantmentStorageMeta.class, enchantmentStorageMeta -> {
            for (ResolvableEnchantmentEntry entry : this.getStoredEnchantments()) {
                AbstractMap.SimpleEntry<Enchantment, Integer> resolvedEntry = entry.resolve(context);
                if (resolvedEntry != null) {
                    enchantmentStorageMeta.addStoredEnchant(resolvedEntry.getKey(), resolvedEntry.getValue(), true);
                }
            }
        });
        if (!apply && Configuration.enableDebug)
            Logger.info("Could not apply StoredEnchantmentsComponent to item: " + itemStack.getType().name());
    }
}
