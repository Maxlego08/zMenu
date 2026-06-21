package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.configuration.Configuration;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
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
import java.util.Map;

@SuppressWarnings("unused")
public class StoredEnchantmentsComponent extends ItemComponent {
    private final @NotNull List<ResolvableEnchantmentEntry> storedEnchantments;

    public StoredEnchantmentsComponent(@NotNull List<ResolvableEnchantmentEntry> storedEnchantments) {
        this.storedEnchantments = storedEnchantments;
    }

    public @NotNull List<ResolvableEnchantmentEntry> getStoredEnchantments() {
        return this.storedEnchantments;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        boolean apply = ItemUtil.editMeta(itemStack, EnchantmentStorageMeta.class, enchantmentStorageMeta -> {
            for (ResolvableEnchantmentEntry entry : this.storedEnchantments) {
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
