package fr.maxlego08.menu.api.itemstack.components;

import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.itemstack.ItemComponent;
import fr.maxlego08.menu.api.utils.resolvable.lang.ResolvableBoolean;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EnchantmentGlintOverrideComponent extends ItemComponent {
    private final ResolvableBoolean hasGlint;

    public EnchantmentGlintOverrideComponent(boolean hasGlint) {
        this.hasGlint = ResolvableBoolean.of(hasGlint);
    }

    public EnchantmentGlintOverrideComponent(ResolvableBoolean hasGlint) {
        this.hasGlint = hasGlint;
    }

    public ResolvableBoolean hasGlint() {
        return this.hasGlint;
    }

    @Override
    public void apply(@NotNull BuildContext context, @NotNull ItemStack itemStack, @Nullable Player player) {
        ItemMeta itemMeta = itemStack.getItemMeta();
        if (itemMeta == null) return;

        this.applyResolvable(context, itemMeta::setEnchantmentGlintOverride, this.hasGlint);

        itemStack.setItemMeta(itemMeta);

    }
}
