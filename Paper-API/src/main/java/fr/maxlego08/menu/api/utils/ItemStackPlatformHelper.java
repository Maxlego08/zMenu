package fr.maxlego08.menu.api.utils;

import com.google.common.base.Preconditions;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public abstract class ItemStackPlatformHelper {
    private static ItemStackPlatformHelper helper;

    protected ItemStackPlatformHelper() {
        if (helper != null) {
            throw new IllegalStateException("ItemStackPlatformHelper is already initialized");
        }
        helper = this;
    }

    public static ItemStackPlatformHelper getHelper() {
        if (helper == null) {
            throw new IllegalStateException("ItemStackPlatformHelper is not initialized");
        }
        return helper;
    }

    // --- Display Name ---

    public abstract boolean hasDisplayName(@NotNull ItemStack itemStack);

    @Nullable
    public abstract String getDisplayName(@NotNull ItemStack itemStack);

    // --- Lore ---

    public abstract boolean hasLore(@NotNull ItemStack itemStack);

    @NotNull
    public abstract List<@NotNull String> getLore(@NotNull ItemStack itemStack);

    // --- Custom Model Data ---

    public boolean hasCustomModelData(@NotNull ItemStack itemStack) {
        Preconditions.checkNotNull(itemStack, "itemStack cannot be null");
        if (!itemStack.hasItemMeta()) {
            return false;
        }
        return itemStack.getItemMeta().hasCustomModelData();
    }

    public int getCustomModelData(@NotNull ItemStack itemStack) {
        return itemStack.getItemMeta().getCustomModelData();
    }
}
