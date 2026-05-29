package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.RuleContext;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public class ZRuleContext implements RuleContext {
    private final ItemStack itemStack;
    private final Material material;
    private final String displayName;

    public ZRuleContext(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.material = itemStack.getType();
        this.displayName = itemStack.hasItemMeta() && itemStack.getItemMeta().hasDisplayName() ? itemStack.getItemMeta().getDisplayName() : null;
    }

    @Override
    public ItemStack getItemStack() {
        return itemStack;
    }

    @Override
    public Material getMaterial() {
        return material;
    }

    @Override
    public @Nullable String getDisplayName() {
        return displayName;
    }
}
