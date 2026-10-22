package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.ItemEnchantments;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.LinkedHashMap;
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
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (ResolvableEnchantmentEntry entry : this.storedEnchantments) {
            if (!(entry.serialize() instanceof Map<?, ?> entryMap)) {
                throw new UnsupportedOperationException("The stored enchantment " + entry + " cannot be serialized as a map");
            }
            entryMap.forEach((key, value) -> map.put(String.valueOf(key), value));
        }
        return map;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemEnchantments.Builder storedEnchantments = ItemEnchantments.itemEnchantments();
        ItemEnchantments current = itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS);
        if (current != null) {
            storedEnchantments.addAll(current.enchantments());
        }

        boolean added = false;
        for (ResolvableEnchantmentEntry entry : this.storedEnchantments) {
            AbstractMap.SimpleEntry<Enchantment, Integer> resolvedEntry = entry.resolve(context);
            if (resolvedEntry != null) {
                storedEnchantments.add(resolvedEntry.getKey(), resolvedEntry.getValue());
                added = true;
            }
        }

        if (added) {
            itemStack.setData(DataComponentTypes.STORED_ENCHANTMENTS, storedEnchantments.build());
        }
    }
}
