package fr.maxlego08.menu.api;

import fr.maxlego08.menu.api.attribute.IAttribute;
import fr.maxlego08.menu.api.enums.MenuItemRarity;
import fr.maxlego08.menu.api.itemstack.*;
import fr.maxlego08.menu.api.utils.LoreType;
import fr.maxlego08.menu.api.utils.Placeholders;
import org.bukkit.OfflinePlayer;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

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
     * Build an item stack using the given player, use cache and placeholders with a default base itemStack.
     *
     * @param player                The player to use to build the item stack.
     * @param useCache              Whether to use the cache or not.
     * @param placeholders          The placeholders to use in the item stack construction.
     * @param defaultItemStack      The base itemStack build
     * @return The built item stack.
     */
    ItemStack build(Player player, boolean useCache, Placeholders placeholders, ItemStack defaultItemStack);

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

    String getAmount();

    void setAmount(String amount);

    String getUrl();

    void setUrl(String url);

    String getData();

    void setData(String data);

    /**
     * This méthod is deprecated, now use placeholder support
     * Use getDura or parseDura
     * @return durability
     */
    @Deprecated
    int getDurability();

    String getDura();

    int parseDura(Player player);

    int parseDura(OfflinePlayer offlinePlayer, Placeholders placeholders);

    void setDurability(int durability);

    void setDurability(String durability);

    Potion getPotion();

    void setPotion(Potion potion);

    List<String> getLore();

    void setLore(List<String> lore);

    List<ItemFlag> getFlags();

    void setFlags(List<ItemFlag> flags);

    String getDisplayName();

    void setDisplayName(String displayName);

    boolean isGlowing();

    void setGlowing(boolean isGlowing);

    String getModelID();

    void setModelID(String modelID);

    void setModelID(int modelID);

    Map<Enchantment, Integer> getEnchantments();

    void setEnchantments(Map<Enchantment, Integer> enchantments);

    List<IAttribute> getAttributes();

    void setAttributes(List<IAttribute> attributes);

    Banner getBanner();

    void setBanner(Banner banner);

    Firework getFirework();

    void setFirework(Firework firework);

    LeatherArmor getLeatherArmor();

    void setLeatherArmor(LeatherArmor leatherArmor);

    String getToolTipStyle();

    void setToolTipStyle(String toolTipStyle);

    Map<String, String> getTranslatedDisplayName();

    void setTranslatedDisplayName(Map<String, String> translatedDisplayName);

    Map<String, List<String>> getTranslatedLore();

    void setTranslatedLore(Map<String, List<String>> translatedLore);

    boolean isNeedPlaceholderAPI();

    void setNeedPlaceholderAPI(boolean needPlaceholderAPI);

    ItemStack getCacheItemStack();

    void setCacheItemStack(ItemStack cacheItemStack);

    int getMaxStackSize();

    void setMaxStackSize(int maxStackSize);

    int getMaxDamage();

    void setMaxDamage(int maxDamage);

    int getDamage();

    void setDamage(int damage);

    int getRepairCost();

    void setRepairCost(int repairCost);

    boolean isUnbreakableEnabled();

    void setUnbreakableEnabled(Boolean unbreakableEnabled);

    boolean isUnbreakableShowInTooltip();

    void setUnbreakableShowInTooltip(Boolean unbreakableShowInTooltip);

    boolean isFireResistant();

    void setFireResistant(Boolean fireResistant);

    boolean isHideTooltip();

    void setHideTooltip(Boolean hideTooltip);

    boolean isHideAdditionalTooltip();

    void setHideAdditionalTooltip(Boolean hideAdditionalTooltip);

    boolean isEnchantmentGlint();

    void setEnchantmentGlint(Boolean enchantmentGlint);

    boolean isEnchantmentShowInTooltip();

    void setEnchantmentShowInTooltip(Boolean enchantmentShowInTooltip);

    boolean isAttributeShowInTooltip();

    void setAttributeShowInTooltip(Boolean attributeShowInTooltip);

    TrimConfiguration getTrimConfiguration();

    void setTrimConfiguration(TrimConfiguration trimConfiguration);

    MenuItemRarity getItemRarity();

    void setItemRarity(MenuItemRarity itemRarity);

    boolean isCenterLore();

    void setCenterLore(boolean centerLore);

    boolean isCenterName();

    void setCenterName(boolean centerName);

    LoreType getLoreType();

    void setLoreType(LoreType loreType);

    int parseAmount(Player player);

    int parseAmount(Player player, Placeholders placeholders);

    int parseAmount(OfflinePlayer offlinePlayer, Placeholders placeholders);

    String getItemModel();

    void setItemModel(String itemModel);

    void setEquippedModel(String equippedModel);

    String getEquippedModel();

    boolean isClearDefaultAttributes();

    void setClearDefaultAttributes(boolean clearDefaultAttributes);
}
