package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class EnchantementsComponent extends ItemComponent {
    private final @NotNull List<ResolvableEnchantmentEntry> enchantments;

    public EnchantementsComponent(@NotNull List<ResolvableEnchantmentEntry> enchantments) {
        this.enchantments = enchantments;
    }

    public @NotNull List<ResolvableEnchantmentEntry> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Map<Enchantment, Integer> resolved = new HashMap<>();
        for (ResolvableEnchantmentEntry entry : this.enchantments) {
            AbstractMap.SimpleEntry<Enchantment, Integer> resolvedEntry = entry.resolve(context);
            if (resolvedEntry != null) {
                resolved.put(resolvedEntry.getKey(), resolvedEntry.getValue());
            }
        }
        if (!resolved.isEmpty()) {
            itemStack.addEnchantments(resolved);
        }
    }
}
