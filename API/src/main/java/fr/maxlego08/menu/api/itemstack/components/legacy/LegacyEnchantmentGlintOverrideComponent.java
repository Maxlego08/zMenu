package fr.maxlego08.menu.api.itemstack.components.legacy;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.components.EnchantmentGlintOverrideComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LegacyEnchantmentGlintOverrideComponent extends EnchantmentGlintOverrideComponent {

    public LegacyEnchantmentGlintOverrideComponent(ResolvableBoolean hasGlint) {
        super(hasGlint);
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setEnchantmentGlintOverride, this.hasGlint());

        itemStack.setItemMeta(itemMeta);
    }
}
