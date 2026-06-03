package fr.maxlego08.menu.api.rules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface RuleContext {

    ItemStack getItemStack();

    Material getMaterial();

    boolean hasDisplayName();

    @Nullable
    String getDisplayName();

    boolean hasLore();

    @NotNull
    List<String> getLore();

    boolean hasCustomModelData();

    int getCustomModelData();
}
