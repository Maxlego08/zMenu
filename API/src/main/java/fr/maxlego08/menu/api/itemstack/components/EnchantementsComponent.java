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

import java.util.*;

@SuppressWarnings("unused")
public class EnchantementsComponent extends ItemComponent {
    private final @NotNull List<ResolvableEnchantmentEntry> enchantments;

    public EnchantementsComponent(@NotNull List<ResolvableEnchantmentEntry> enchantments) {
        this.enchantments = enchantments;
    }

    public @NotNull List<ResolvableEnchantmentEntry> getEnchantments() {
        return this.enchantments;
    }

    protected @NotNull Map<Enchantment, Integer> resolveEnchantments(@NotNull BuildContext context) {
        Map<Enchantment, Integer> resolved = new HashMap<>();
        for (ResolvableEnchantmentEntry entry : this.enchantments) {
            AbstractMap.SimpleEntry<Enchantment, Integer> resolvedEntry = entry.resolve(context);
            if (resolvedEntry != null) {
                resolved.put(resolvedEntry.getKey(), resolvedEntry.getValue());
            }
        }
        return resolved;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Map<Enchantment, Integer> resolved = this.resolveEnchantments(context);
        if (resolved.isEmpty()) return;

        ItemEnchantments.Builder enchantments = ItemEnchantments.itemEnchantments();
        ItemEnchantments current = itemStack.getData(DataComponentTypes.ENCHANTMENTS);
        if (current != null) {
            enchantments.addAll(current.enchantments());
        }
        enchantments.addAll(resolved);

        itemStack.setData(DataComponentTypes.ENCHANTMENTS, enchantments.build());
    }

    /**
     * Each entry writes itself as {@code enchantment: level}; the section holds them all.
     */
    @Override
    public @Nullable Object serialize() {
        Map<String, Object> map = new LinkedHashMap<>();
        for (ResolvableEnchantmentEntry entry : this.enchantments) {
            if (!(entry.serialize() instanceof Map<?, ?> serializedEntry)) {
                throw new UnsupportedOperationException("The enchantment entry " + entry + " cannot be serialized");
            }
            serializedEntry.forEach((enchantment, level) -> map.put(String.valueOf(enchantment), level));
        }
        return map;
    }
}
