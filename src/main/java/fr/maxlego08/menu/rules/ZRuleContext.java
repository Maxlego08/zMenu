package fr.maxlego08.menu.rules;

import fr.maxlego08.menu.api.rules.ItemRuleContext;
import fr.maxlego08.menu.api.utils.ItemStackPlatformHelper;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ZRuleContext implements ItemRuleContext {
    private final ItemStack itemStack;

    private final Material material;

    private final String displayName;
    private final boolean hasDisplayName;

    private final List<String> lore;
    private final boolean hasLore;

    private final int customModelData;
    private final boolean hasCustomModelData;

    public ZRuleContext(ItemStack itemStack) {
        this.itemStack = itemStack;
        this.material = itemStack.getType();

        ItemStackPlatformHelper helper = ItemStackPlatformHelper.getHelper();

        this.displayName = helper.getDisplayName(itemStack);
        this.hasDisplayName = helper.hasDisplayName(itemStack);

        this.lore = helper.getLore(itemStack);
        this.hasLore = helper.hasLore(itemStack);

        this.customModelData = helper.hasCustomModelData(itemStack) ? helper.getCustomModelData(itemStack) : 0;
        this.hasCustomModelData = helper.hasCustomModelData(itemStack);
    }

    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public Material getMaterial() {
        return this.material;
    }

    @Override
    public boolean hasDisplayName() {
        return this.hasDisplayName;
    }

    @Override
    public @Nullable String getDisplayName() {
        return this.displayName;
    }

    @Override
    public boolean hasLore() {
        return this.hasLore;
    }

    @Override
    public @NotNull List<String> getLore() {
        return this.lore;
    }

    @Override
    public boolean hasCustomModelData() {
        return this.hasCustomModelData;
    }

    @Override
    public int getCustomModelData() {
        return this.customModelData;
    }
}
