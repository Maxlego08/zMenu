package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.attribute.AttributeMergeStrategy;
import fr.maxlego08.menu.api.attribute.AttributeWrapper;
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

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface MenuItemStack {

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
     * Retrieves the {@link InventoryManager} responsible for managing this menu item stack.
     *
     * @return The inventory manager that manages this menu item stack.
     */
    InventoryManager getInventoryManager();

    /**
     * Returns the path of the configuration file associated with the menu item stack.
     *
     * @return The path of the configuration file.
     */
    String getFilePath();

    /**
     * Returns the path of the configuration file associated with the menu item stack,
     * relative to the root of the configuration folder.
     *
     * @return The path of the configuration file relative to the root of the configuration folder.
     */
    String getPath();

    /**
     * Returns the material of the item stack.
     *
     * @return The material of the item stack.
     */
    String getMaterial();

    /**
     * Sets the material of the item stack.
     *
     * @param material The new material.
     */
    void setMaterial(String material);

    /**
     * Retrieves the target player placeholder used in the menu item stack,
     * or {@code null} if no target player placeholder is set.
     *
     * @return The target player placeholder, or {@code null} if none is set.
     */
    String getTargetPlayer();

    /**
     * Sets the target player placeholder for the menu item stack.
     * This placeholder is used to represent the target player in the menu configurations.
     *
     * @param targetPlayer The target player placeholder to set.
     */
    void setTargetPlayer(String targetPlayer);

    /**
     * Retrieves the configured amount placeholder for the item stack.
     *
     * @return the amount expression as a string.
     */
    String getAmount();

    /**
     * Updates the configured amount placeholder for the item stack.
     *
     * @param amount the amount expression to set.
     */
    void setAmount(String amount);

    /**
     * Returns the skull texture URL associated with this item stack, when applicable.
     *
     * @return the skull URL or {@code null} if none is set.
     */
    String getUrl();

    /**
     * Defines the skull texture URL for this item stack.
     *
     * @param url the skull URL to use.
     */
    void setUrl(String url);

    /**
     * Retrieves the legacy data value configured for this item stack.
     *
     * @return the data value.
     */
    String getData();

    /**
     * Sets the legacy data value for this item stack.
     *
     * @param data the data value to apply.
     */
    void setData(String data);

    /**
     * Returns the custom durability configured for this item stack.
     *
     * @return the durability value.
     */
    int getDurability();

    /**
     * Sets the durability of the item stack.
     *
     * @param durability the durability to apply.
     */
    void setDurability(int durability);

    /**
     * Retrieves the potion configuration applied to this item stack.
     *
     * @return the potion descriptor, or {@code null} when none is set.
     */
    Potion getPotion();

    /**
     * Applies a potion configuration to this item stack.
     *
     * @param potion the potion descriptor to use.
     */
    void setPotion(Potion potion);

    /**
     * Returns the lore lines attached to this item stack.
     *
     * @return the lore lines.
     */
    List<String> getLore();

    /**
     * Replaces the lore lines attached to this item stack.
     *
     * @param lore the lore lines to apply.
     */
    void setLore(List<String> lore);

    /**
     * Retrieves the item flags applied to this item stack.
     *
     * @return the list of item flags.
     */
    List<ItemFlag> getFlags();

    /**
     * Sets the item flags applied to this item stack.
     *
     * @param flags the flags to set.
     */
    void setFlags(List<ItemFlag> flags);

    /**
     * Returns the display name configured for the item stack.
     *
     * @return the display name.
     */
    String getDisplayName();

    /**
     * Defines the display name for the item stack.
     *
     * @param displayName the display name to set.
     */
    void setDisplayName(String displayName);

    /**
     * Indicates whether the item should appear with a glowing effect.
     *
     * @return {@code true} if the glowing effect is enabled.
     */
    boolean isGlowing();

    /**
     * Enables or disables the glowing effect on the item.
     *
     * @param isGlowing {@code true} to enable the effect.
     */
    void setGlowing(boolean isGlowing);

    /**
     * Retrieves the custom model data identifier as a string.
     *
     * @return the model identifier.
     */
    String getModelID();

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
     * Returns the enchantments configured for this item stack.
     *
     * @return the enchantments map.
     */
    Map<Enchantment, Integer> getEnchantments();

    /**
     * Applies the provided enchantments to this item stack.
     *
     * @param enchantments the enchantments to set.
     */
    void setEnchantments(Map<Enchantment, Integer> enchantments);

    /**
     * Retrieves the attributes configured for this item stack.
     *
     * @return the list of attributes.
     */
    List<AttributeWrapper> getAttributes();

    /**
     * Sets the attributes applied to this item stack.
     *
     * @param attributes the attributes to apply.
     */
    void setAttributes(List<AttributeWrapper> attributes);

    /**
     * Retrieves the strategy for merging attribute modifiers when applying custom attributes to items.
     *
     * @return the strategy for merging attribute modifiers.
     * @see AttributeMergeStrategy
     */
    AttributeMergeStrategy getAttributeMergeStrategy();

    /**
     * Sets the strategy for merging attribute modifiers when applying custom attributes to items.
     * The chosen strategy will be used when calling {@link #setAttributes(List)}.
     *
     * @param attributeMergeStrategy the strategy for merging attribute modifiers.
     * @see AttributeMergeStrategy
     */
    void setAttributeMergeStrategy(AttributeMergeStrategy attributeMergeStrategy);

    /**
     * Returns the banner configuration attached to this item stack.
     *
     * @return the banner descriptor, or {@code null} when none is set.
     */
    Banner getBanner();

    /**
     * Applies a banner configuration to this item stack.
     *
     * @param banner the banner descriptor to use.
     */
    void setBanner(Banner banner);

    /**
     * Retrieves the firework configuration attached to this item stack.
     *
     * @return the firework descriptor, or {@code null} when none is set.
     */
    Firework getFirework();

    /**
     * Applies a firework configuration to this item stack.
     *
     * @param firework the firework descriptor to use.
     */
    void setFirework(Firework firework);

    /**
     * Retrieves the leather armor configuration attached to this item stack.
     *
     * @return the leather armor descriptor, or {@code null} when none is set.
     */
    LeatherArmor getLeatherArmor();

    /**
     * Applies a leather armor configuration to this item stack.
     *
     * @param leatherArmor the leather armor descriptor to use.
     */
    void setLeatherArmor(LeatherArmor leatherArmor);

    /**
     * Returns the tooltip style identifier applied to this item stack.
     *
     * @return the tooltip style key.
     */
    String getToolTipStyle();

    /**
     * Sets the tooltip style identifier applied to this item stack.
     *
     * @param toolTipStyle the tooltip style key to use.
     */
    void setToolTipStyle(String toolTipStyle);

    /**
     * Retrieves the translated display names available for this item stack.
     *
     * @return the map of locale to display name.
     */
    Map<String, String> getTranslatedDisplayName();

    /**
     * Sets the translated display names available for this item stack.
     *
     * @param translatedDisplayName the map of locale to display name.
     */
    void setTranslatedDisplayName(Map<String, String> translatedDisplayName);

    /**
     * Retrieves the translated lore entries available for this item stack.
     *
     * @return the map of locale to lore lines.
     */
    Map<String, List<String>> getTranslatedLore();

    /**
     * Sets the translated lore entries available for this item stack.
     *
     * @param translatedLore the map of locale to lore lines.
     */
    void setTranslatedLore(Map<String, List<String>> translatedLore);

    /**
     * Indicates whether PlaceholderAPI is required for this item stack.
     *
     * @return {@code true} if PlaceholderAPI is needed.
     */
    boolean isNeedPlaceholderAPI();

    /**
     * Marks whether PlaceholderAPI is required for this item stack.
     *
     * @param needPlaceholderAPI {@code true} when PlaceholderAPI is needed.
     */
    void setNeedPlaceholderAPI(boolean needPlaceholderAPI);

    /**
     * Retrieves the cached item stack when caching is enabled.
     *
     * @return the cached ItemStack, or {@code null} when none exists.
     */
    ItemStack getCacheItemStack();

    /**
     * Sets the cached item stack value used when caching is enabled.
     *
     * @param cacheItemStack the ItemStack to cache.
     */
    void setCacheItemStack(ItemStack cacheItemStack);

    /**
     * Retrieves the configured maximum stack size for the item.
     *
     * @return the maximum stack size.
     */
    int getMaxStackSize();

    /**
     * Sets the maximum stack size for the item.
     *
     * @param maxStackSize the new maximum stack size.
     */
    void setMaxStackSize(int maxStackSize);

    /**
     * Retrieves the configured maximum damage for the item.
     *
     * @return the maximum damage value.
     */
    int getMaxDamage();

    /**
     * Sets the maximum damage for the item.
     *
     * @param maxDamage the maximum damage value.
     */
    void setMaxDamage(int maxDamage);

    /**
     * Retrieves the current damage value for the item.
     *
     * @return the damage value.
     */
    int getDamage();

    /**
     * Sets the current damage value for the item.
     *
     * @param damage the damage to apply.
     */
    void setDamage(int damage);

    /**
     * Retrieves the repair cost associated with the item.
     *
     * @return the repair cost.
     */
    int getRepairCost();

    /**
     * Sets the repair cost associated with the item.
     *
     * @param repairCost the repair cost value.
     */
    void setRepairCost(int repairCost);

    /**
     * Indicates whether the item is unbreakable.
     *
     * @return {@code true} if unbreakable is enabled.
     */
    boolean isUnbreakableEnabled();

    /**
     * Enables or disables the unbreakable flag on the item.
     *
     * @param unbreakableEnabled {@code true} to enable the flag.
     */
    void setUnbreakableEnabled(Boolean unbreakableEnabled);

    /**
     * Indicates whether the unbreakable tooltip should be shown.
     *
     * @return {@code true} if the tooltip is shown.
     */
    boolean isUnbreakableShowInTooltip();

    /**
     * Controls the visibility of the unbreakable tooltip.
     *
     * @param unbreakableShowInTooltip {@code true} to show the tooltip.
     */
    void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip);

    /**
     * Indicates whether the item is fire resistant.
     *
     * @return {@code true} if fire resistance is enabled.
     */
    boolean isFireResistant();

    /**
     * Enables or disables fire resistance on the item.
     *
     * @param fireResistant {@code true} to enable fire resistance.
     */
    void setFireResistant(Boolean fireResistant);

    /**
     * Indicates whether the tooltip should be hidden.
     *
     * @return {@code true} if the tooltip is hidden.
     */
    boolean isHideTooltip();

    /**
     * Controls the visibility of the tooltip.
     *
     * @param hideTooltip {@code true} to hide the tooltip.
     */
    void setHideTooltip(Boolean hideTooltip);

    /**
     * Indicates whether additional tooltip information should be hidden.
     *
     * @return {@code true} if additional tooltip content is hidden.
     */
    boolean isHideAdditionalTooltip();

    /**
     * Controls whether additional tooltip information is hidden.
     *
     * @param hideAdditionalTooltip {@code true} to hide additional tooltip content.
     */
    void setHideAdditionalTooltip(Boolean hideAdditionalTooltip);

    /**
     * Indicates whether the enchantment glint is enabled.
     *
     * @return {@code true} if the glint is enabled.
     */
    boolean isEnchantmentGlint();

    /**
     * Enables or disables the enchantment glint.
     *
     * @param enchantmentGlint {@code true} to enable the glint.
     */
    void setEnchantmentGlint(Boolean enchantmentGlint);

    /**
     * Indicates whether enchantments should be visible in the tooltip.
     *
     * @return {@code true} if enchantments are visible.
     */
    boolean isEnchantmentShowInTooltip();

    /**
     * Controls the visibility of enchantments in the tooltip.
     *
     * @param enchantmentShowInTooltip {@code true} to show enchantments.
     */
    void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip);

    /**
     * Indicates whether attributes should be visible in the tooltip.
     *
     * @return {@code true} if attributes are visible.
     */
    boolean isAttributeShowInTooltip();

    /**
     * Controls the visibility of attributes in the tooltip.
     *
     * @param attributeShowInTooltip {@code true} to show attributes.
     */
    void setAttributeShowInTooltip(Boolean attributeShowInTooltip);

    /**
     * Retrieves the trim configuration applied to armor items.
     *
     * @return the trim configuration, or {@code null} when none is set.
     */
    TrimConfiguration getTrimConfiguration();

    /**
     * Sets the trim configuration applied to armor items.
     *
     * @param trimConfiguration the trim configuration to apply.
     */
    void setTrimConfiguration(TrimConfiguration trimConfiguration);

    /**
     * Retrieves the rarity assigned to this item stack.
     *
     * @return the item rarity value.
     */
    MenuItemRarity getItemRarity();

    /**
     * Sets the rarity assigned to this item stack.
     *
     * @param itemRarity the rarity to apply.
     */
    void setItemRarity(MenuItemRarity itemRarity);

    /**
     * Indicates whether the lore should be centered when displayed.
     *
     * @return {@code true} if lore centering is enabled.
     */
    boolean isCenterLore();

    /**
     * Enables or disables lore centering.
     *
     * @param centerLore {@code true} to center lore.
     */
    void setCenterLore(boolean centerLore);

    /**
     * Indicates whether the display name should be centered when displayed.
     *
     * @return {@code true} if name centering is enabled.
     */
    boolean isCenterName();

    /**
     * Enables or disables display name centering.
     *
     * @param centerName {@code true} to center the name.
     */
    void setCenterName(boolean centerName);

    /**
     * Retrieves how lore should be merged when applied.
     *
     * @return the lore merge strategy.
     */
    LoreType getLoreType();

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
     * Retrieves the item model identifier used for newer rendering APIs.
     *
     * @return the item model identifier.
     */
    String getItemModel();

    /**
     * Sets the item model identifier used for newer rendering APIs.
     *
     * @param itemModel the item model identifier to set.
     */
    void setItemModel(String itemModel);

    /**
     * Retrieves the equipped model identifier used for wearable items.
     *
     * @return the equipped model identifier.
     */
    String getEquippedModel();

    /**
     * Sets the equipped model identifier used for wearable items.
     *
     * @param equippedModel the equipped model identifier to set.
     */
    void setEquippedModel(String equippedModel);

    /**
     * Indicates whether default attributes should be cleared on the item.
     *
     * @return {@code true} if default attributes are cleared.
     */
    boolean isClearDefaultAttributes();

    /**
     * Controls whether default attributes should be cleared on the item.
     *
     * @param clearDefaultAttributes {@code true} to clear default attributes.
     */
    void setClearDefaultAttributes(boolean clearDefaultAttributes);

    Collection<@NotNull ItemComponent> getItemComponents();

    void addItemComponent(@NotNull ItemComponent itemMetadata);
}
