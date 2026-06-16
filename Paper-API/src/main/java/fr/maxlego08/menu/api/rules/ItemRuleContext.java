package fr.maxlego08.menu.api.rules;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface ItemRuleContext {

    /**
     * @return the original ItemStack being evaluated
     */
    ItemStack getItemStack();

    /**
     * @return the material of the item
     */
    Material getMaterial();

    /**
     * @return true if the item has a custom display name
     */
    boolean hasDisplayName();

    /**
     * @return the display name or null if no custom name
     */
    @Nullable
    String getDisplayName();

    /**
     * @return true if the item has lore
     */
    boolean hasLore();

    /**
     * @return the lore lines or empty list if no lore
     */
    @NotNull
    List<String> getLore();

    /**
     * @return true if the item has custom model data
     */
    boolean hasCustomModelData();

    /**
     * @return the custom model data, or 0 if not set
     */
    int getCustomModelData();
}
