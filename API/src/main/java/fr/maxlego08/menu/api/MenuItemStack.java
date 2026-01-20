package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
import fr.maxlego08.menu.api.context.BuildContext;
import fr.maxlego08.menu.api.context.MenuItemStackContext;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.*;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

public interface MenuItemStack extends MenuItemStackContext {

    /**
     * Build an item stack using the provided build context.
     * This method constructs an ItemStack instance based on the given context,
     * applying any necessary configurations and customizations.
     *
     * @param context The build context containing relevant information.
     * @return The constructed ItemStack.
     */
    ItemStack build(BuildContext context);

    /**
     * Build an item stack using the provided player context.
     * This method constructs an ItemStack instance for the player,
     * applying any necessary configurations and customizations.
     *
     * @param player The player for whom the item stack is being built.
     * @return The constructed ItemStack for the specified player.
     */
    ItemStack build(Player player);

    /**
     * Build an item stack using the given player and whether to use the cache or not.
     *
     * @param player   The player to use to build the item stack.
     * @param useCache Whether to use the cache or not.
     * @return The built item stack.
     */
    ItemStack build(Player player, boolean useCache);

    /**
     * Build an item stack using the given player, use cache and placeholders.
     *
     * @param player       The player to use to build the item stack.
     * @param useCache     Whether to use the cache or not.
     * @param placeholders The placeholders to use in the item stack construction.
     * @return The built item stack.
     */
    ItemStack build(Player player, boolean useCache, Placeholders placeholders);

    /**
     * Sets the material of the item stack.
     *
     * @param material The new material.
     */
    void setMaterial(String material);

    /**
     * Sets the target player placeholder for the menu item stack.
     * This placeholder is used to represent the target player in the menu configurations.
     *
     * @param targetPlayer The target player placeholder to set.
     */
    void setTargetPlayer(String targetPlayer);

    /**
     * Updates the configured amount placeholder for the item stack.
     *
     * @param amount the amount expression to set.
     */
    void setAmount(String amount);

    /**
     * Defines the skull texture URL for this item stack.
     *
     * @param url the skull URL to use.
     */
    void setUrl(String url);

    /**
     * Sets the legacy data value for this item stack.
     *
     * @param data the data value to apply.
     */
    void setData(String data);

    /**
     * Sets the durability of the item stack.
     *
     * @param durability the durability to apply.
     */
    void setDurability(int durability);

    /**
     * Applies a potion configuration to this item stack.
     *
     * @param potion the potion descriptor to use.
     */
    void setPotion(Potion potion);

    /**
     * Replaces the lore lines attached to this item stack.
     *
     * @param lore the lore lines to apply.
     */
    void setLore(List<String> lore);

    /**
     * Sets the item flags applied to this item stack.
     *
     * @param flags the flags to set.
     */
    void setFlags(List<ItemFlag> flags);

    /**
     * Defines the display name for the item stack.
     *
     * @param displayName the display name to set.
     */
    void setDisplayName(String displayName);

    /**
     * Enables or disables the glowing effect on the item.
     *
     * @param isGlowing {@code true} to enable the effect.
     */
    void setGlowing(boolean isGlowing);

    /**
     * Sets the custom model data identifier using a string value.
     *
     * @param modelID the identifier to use.
     */
    void setModelID(String modelID);

    /**
     * Sets the custom model data identifier using an integer value.
     *
     * @param modelID the identifier to use.
     */
    void setModelID(int modelID);

    /**
     * Applies the provided enchantments to this item stack.
     *
     * @param enchantments the enchantments to set.
     */
    void setEnchantments(Map<Enchantment, Integer> enchantments);

    /**
     * Sets the attributes applied to this item stack.
     *
     * @param attributes the attributes to apply.
     */
    void setAttributes(List<AttributeWrapper> attributes);

    /**
     * Sets the strategy for merging attribute modifiers when applying custom attributes to items.
     * The chosen strategy will be used when calling {@link #setAttributes(List)}.
     *
     * @param attributeMergeStrategy the strategy for merging attribute modifiers.
     * @see AttributeMergeStrategy
     */
    void setAttributeMergeStrategy(AttributeMergeStrategy attributeMergeStrategy);

    /**
     * Applies a banner configuration to this item stack.
     *
     * @param banner the banner descriptor to use.
     */
    void setBanner(Banner banner);

    /**
     * Applies a firework configuration to this item stack.
     *
     * @param firework the firework descriptor to use.
     */
    void setFirework(Firework firework);

    /**
     * Applies a leather armor configuration to this item stack.
     *
     * @param leatherArmor the leather armor descriptor to use.
     */
    void setLeatherArmor(LeatherArmor leatherArmor);

    /**
     * Sets the tooltip style identifier applied to this item stack.
     *
     * @param toolTipStyle the tooltip style key to use.
     */
    void setToolTipStyle(String toolTipStyle);

    /**
     * Sets the translated display names available for this item stack.
     *
     * @param translatedDisplayName the map of locale to display name.
     */
    void setTranslatedDisplayName(Map<String, String> translatedDisplayName);

    /**
     * Sets the translated lore entries available for this item stack.
     *
     * @param translatedLore the map of locale to lore lines.
     */
    void setTranslatedLore(Map<String, List<String>> translatedLore);

    /**
     * Marks whether PlaceholderAPI is required for this item stack.
     *
     * @param needPlaceholderAPI {@code true} when PlaceholderAPI is needed.
     */
    void setNeedPlaceholderAPI(boolean needPlaceholderAPI);

    /**
     * Sets the cached item stack value used when caching is enabled.
     *
     * @param cacheItemStack the ItemStack to cache.
     */
    void setCacheItemStack(ItemStack cacheItemStack);

    /**
     * Sets the maximum stack size for the item.
     *
     * @param maxStackSize the new maximum stack size.
     */
    void setMaxStackSize(int maxStackSize);

    /**
     * Sets the maximum damage for the item.
     *
     * @param maxDamage the maximum damage value.
     */
    void setMaxDamage(int maxDamage);

    /**
     * Sets the current damage value for the item.
     *
     * @param damage the damage to apply.
     */
    void setDamage(int damage);

    /**
     * Sets the repair cost associated with the item.
     *
     * @param repairCost the repair cost value.
     */
    void setRepairCost(int repairCost);

    /**
     * Enables or disables the unbreakable flag on the item.
     *
     * @param unbreakableEnabled {@code true} to enable the flag.
     */
    void setUnbreakableEnabled(Boolean unbreakableEnabled);

    /**
     * Controls the visibility of the unbreakable tooltip.
     *
     * @param unbreakableShowInTooltip {@code true} to show the tooltip.
     */
    void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip);

    /**
     * Enables or disables fire resistance on the item.
     *
     * @param fireResistant {@code true} to enable fire resistance.
     */
    void setFireResistant(Boolean fireResistant);

    /**
     * Controls the visibility of the tooltip.
     *
     * @param hideTooltip {@code true} to hide the tooltip.
     */
    void setHideTooltip(Boolean hideTooltip);

    /**
     * Controls whether additional tooltip information is hidden.
     *
     * @param hideAdditionalTooltip {@code true} to hide additional tooltip content.
     */
    void setHideAdditionalTooltip(Boolean hideAdditionalTooltip);

    /**
     * Enables or disables the enchantment glint.
     *
     * @param enchantmentGlint {@code true} to enable the glint.
     */
    void setEnchantmentGlint(Boolean enchantmentGlint);

    /**
     * Controls the visibility of enchantments in the tooltip.
     *
     * @param enchantmentShowInTooltip {@code true} to show enchantments.
     */
    void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip);

    /**
     * Controls the visibility of attributes in the tooltip.
     *
     * @param attributeShowInTooltip {@code true} to show attributes.
     */
    void setAttributeShowInTooltip(Boolean attributeShowInTooltip);

    /**
     * Sets the trim configuration applied to armor items.
     *
     * @param trimConfiguration the trim configuration to apply.
     */
    void setTrimConfiguration(TrimConfiguration trimConfiguration);

    /**
     * Sets the rarity assigned to this item stack.
     *
     * @param itemRarity the rarity to apply.
     */
    void setItemRarity(MenuItemRarity itemRarity);

    /**
     * Enables or disables lore centering.
     *
     * @param centerLore {@code true} to center lore.
     */
    void setCenterLore(boolean centerLore);

    /**
     * Enables or disables display name centering.
     *
     * @param centerName {@code true} to center the name.
     */
    void setCenterName(boolean centerName);

    /**
     * Sets how lore should be merged when applied.
     *
     * @param loreType the lore merge strategy.
     */
    void setLoreType(LoreType loreType);

    /**
     * Parses the configured amount for the provided player.
     *
     * @param player the player context.
     * @return the parsed amount.
     */
    int parseAmount(Player player);

    /**
     * Parses the configured amount for the provided player using placeholders.
     *
     * @param player       the player context.
     * @param placeholders placeholder values to apply.
     * @return the parsed amount.
     */
    int parseAmount(Player player, Placeholders placeholders);

    /**
     * Parses the configured amount for the provided offline player using placeholders.
     *
     * @param offlinePlayer the offline player context.
     * @param placeholders  placeholder values to apply.
     * @return the parsed amount.
     */
    int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders);

    /**
     * Sets the item model identifier used for newer rendering APIs.
     *
     * @param itemModel the item model identifier to set.
     */
    void setItemModel(String itemModel);

    /**
     * Sets the equipped model identifier used for wearable items.
     *
     * @param equippedModel the equipped model identifier to set.
     */
    void setEquippedModel(String equippedModel);

    /**
     * Controls whether default attributes should be cleared on the item.
     *
     * @param clearDefaultAttributes {@code true} to clear default attributes.
     */
    void setClearDefaultAttributes(boolean clearDefaultAttributes);

    void addItemComponent(@NotNull ItemComponent itemMetadata);
}
