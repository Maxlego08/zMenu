package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.EnchantementsComponent;
import fr.maxlego08.menu.api.utils.resolvable.bukkit.ResolvableEnchantmentEntry;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

/**
 * The {@code enchantments} component through {@link ItemMeta}, for servers without Paper's data component API.
 */
public class LegacyEnchantementsComponent extends EnchantementsComponent {

    public LegacyEnchantementsComponent(@NotNull List<ResolvableEnchantmentEntry> enchantments) {
        super(enchantments);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        Map<Enchantment, Integer> resolved = this.resolveEnchantments(context);
        if (!resolved.isEmpty()) {
            itemStack.addUnsafeEnchantments(resolved);
        }
    }
}
