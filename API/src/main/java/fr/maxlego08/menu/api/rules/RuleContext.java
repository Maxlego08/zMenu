package fr.maxlego08.menu.api.rules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

public interface RuleContext {

    ItemStack getItemStack();


    Material getMaterial();

    @Nullable
    String getDisplayName();

}
