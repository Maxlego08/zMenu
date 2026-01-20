package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@SuppressWarnings("unused")
public class EnchantementsComponent extends ItemComponent {
    private final @NotNull Map<Enchantment, Integer> enchantments;

    public EnchantementsComponent(@NotNull Map<Enchantment, Integer> enchantments) {
        this.enchantments = enchantments;
    }

    public @NotNull Map<Enchantment, Integer> getEnchantments() {
        return this.enchantments;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        itemStack.addEnchantments(this.enchantments);
    }
}
